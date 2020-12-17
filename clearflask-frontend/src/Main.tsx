import { createMuiTheme, Theme } from '@material-ui/core';
import CssBaseline from '@material-ui/core/CssBaseline';
import { MuiThemeProvider } from '@material-ui/core/styles';
import { ProviderContext } from 'notistack';
import React, { Component, Suspense } from 'react';
import ReactGA from 'react-ga';
import { Provider } from 'react-redux';
import { BrowserRouter, Redirect, Route, Switch } from 'react-router-dom';
import ServerAdmin from './api/serverAdmin';
import { ComponentPropsOverrides } from './app/AppThemeProvider';
import CaptchaChallenger from './app/utils/CaptchaChallenger';
import EnvironmentNotifier from './app/utils/EnvironmentNotifier';
import Loading from './app/utils/Loading';
import MuiSnackbarProvider from './app/utils/MuiSnackbarProvider';
import ServerErrorNotifier from './app/utils/ServerErrorNotifier';
import { closeLoadingScreen } from './common/loadingScreen';
import { detectEnv, Environment, isTracking } from './common/util/detectEnv';
import ScrollAnchor from './common/util/ScrollAnchor';
import { vh } from './common/util/vhUtil';
import windowIso from './common/windowIso';
import HotjarWrapperMain from './site/HotjarWrapperMain';
import IntercomWrapperMain from './site/IntercomWrapperMain';

const notistackRef = React.createRef<ProviderContext>();
export const importSuccess = i => {
  closeLoadingScreen();
  return i;
};
export const importFailed = e => {
  notistackRef.current?.enqueueSnackbar("Network connectivity issues, please reload the page", {
    variant: 'error',
    preventDuplicate: true,
    persist: true,
  });
};
const App = React.lazy(() => import('./app/App'/* webpackChunkName: "app", webpackPrefetch: true */).then(importSuccess).catch(importFailed));
const Dashboard = React.lazy(() => import('./site/Dashboard' /* webpackChunkName: "dashboard" */).then(importSuccess).catch(importFailed));
const Site = React.lazy(() => import('./site/Site'/* webpackChunkName: "site" */).then(importSuccess).catch(importFailed));
const Invoice = React.lazy(() => import('./site/InvoicePage'/* webpackChunkName: "invoice" */).then(importSuccess).catch(importFailed));
const PostStatus = React.lazy(() => import('./app/PostStatus'/* webpackChunkName: "postStatus" */).then(importSuccess).catch(importFailed));

// Prefetch
import('./common/RichEditor'/* webpackChunkName: "RichEditor", webpackPrefetch: true */);
import('./common/EmojiPicker'/* webpackChunkName: "EmojiPicker", webpackPrefetch: true */);

const theme: Theme = createMuiTheme({
  palette: {
    // type: 'dark',
    background: {
      default: '#fff',
      paper: '#fff',
    },
    primary: {
      main: '#218774',
    },
  },
  overrides: {
    MuiAppBar: {
      colorDefault: {
        backgroundColor: '#fff',
      },
    },
  },
  props: {
    ...ComponentPropsOverrides,
  },
});

class Main extends Component {
  customerTrackerPresent: boolean = false;

  constructor(props) {
    super(props);

    if (isTracking() && !windowIso.isSsr) {
      try {
        ReactGA.initialize('UA-127162051-3', {
          gaOptions: {}
        });
        ReactGA.set({
          anonymizeIp: true,
          forceSSL: true
        });
        ReactGA.pageview(windowIso.location.pathname + windowIso.location.search);
      } catch (e) { }
    }
  }

  render() {
    if (!windowIso.isSsr && windowIso.location.hostname === 'www.clearflask.com') {
      // Redirect www to homepage
      return (<Redirect to={windowIso.location.origin.replace(`www.`, '')} />);
    }
    const isProject = this.isProject();
    return (
      // <React.StrictMode>
      <MuiThemeProvider theme={theme}>
        <MuiSnackbarProvider notistackRef={notistackRef}>
          <CssBaseline />
          <ServerErrorNotifier />
          <CaptchaChallenger />
          <div style={{
            minHeight: vh(100),
            display: 'flex',
            flexDirection: 'column',
            background: theme.palette.background.default,
          }}>
            <BrowserRouter>
              <ScrollAnchor scrollOnNavigate />
              {isTracking() && (
                <Route path='/' render={({ location }) => {
                  ReactGA.set({ page: location.pathname + location.search });
                  ReactGA.pageview(location.pathname + location.search);
                  return null;
                }} />
              )}
              <Route render={({ location }) => location.pathname.startsWith('/embed-status') ? null : (
                <EnvironmentNotifier key='env-notifier' />
              )} />
              <Suspense fallback={<Loading />}>
                <Switch>
                  {isProject ? ([(
                    <Route key='embed-status' path="/embed-status/post/:postId" render={props => (
                      <PostStatus
                        {...props}
                        postId={props.match.params['postId'] || ''}
                      />
                    )} />
                  ), (
                    <Route key='app' path="/" render={props => (
                      <App slug={window.location.hostname} {...props} />
                    )} />
                  )]) : ([(
                    <Route key='dashboard' path="/dashboard/:path?/:subPath*" render={props => (
                      <Provider store={ServerAdmin.get().getStore()}>
                        <Dashboard {...props} />
                        <IntercomWrapperMain />
                        <HotjarWrapperMain />
                      </Provider>
                    )} />
                  ), (
                    <Route key='invoice' path="/invoice/:invoiceId" render={props => (
                      <Provider store={ServerAdmin.get().getStore()}>
                        <Invoice invoiceId={props.match.params['invoiceId']} />
                      </Provider>
                    )} />
                  ), (
                    <Route key='site' render={props => (
                      <Provider store={ServerAdmin.get().getStore()}>
                        <Site {...props} />
                        <IntercomWrapperMain />
                        <HotjarWrapperMain />
                      </Provider>
                    )} />
                  )])}
                </Switch>
              </Suspense>
            </BrowserRouter>
          </div>
        </MuiSnackbarProvider>
      </MuiThemeProvider>
      // </React.StrictMode>
    );
  }

  isProject(): boolean {
    switch (detectEnv()) {
      case Environment.PRODUCTION:
        return window.location.hostname !== 'clearflask.com';
      default:
      case Environment.DEVELOPMENT_FRONTEND:
      case Environment.DEVELOPMENT_LOCAL:
        return window.location.hostname !== 'localhost'
          && window.location.hostname !== 'localhost.com';
    }
  }
}

export default Main;
