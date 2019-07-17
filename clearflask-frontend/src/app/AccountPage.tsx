import React, { Component } from 'react';
import { connect } from 'react-redux';
import { ReduxState, Server } from '../api/server';
import * as Client from '../api/client';
import { withStyles, Theme, createStyles, WithStyles } from '@material-ui/core/styles';
import ErrorPage from './ErrorPage';
import DividerCorner from './utils/DividerCorner';
import { Typography, Button, FormControlLabel, Switch, FormHelperText, Grid, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField } from '@material-ui/core';
import WebNotification, { Status as WebNotificationStatus } from '../common/notification/webNotification';
import { withSnackbar, WithSnackbarProps } from 'notistack';
import MobileNotification, { Status as MobileNotificationStatus, Device as MobileNotificationDevice } from '../common/notification/mobileNotification';

const styles = (theme:Theme) => createStyles({
  page: {
    margin: theme.spacing(1),
  },
  item: {
    margin: theme.spacing(4),
  },
});

interface Props {
  server:Server;
}

interface ConnectProps {
  configver?:string;
  config?:Client.Config;
  userMe?:Client.UserMe;
}

interface State {
  deleteDialogOpen?:boolean;
  displayName?:string;
  email?:string;
}

class AccountPage extends Component<Props&ConnectProps&WithStyles<typeof styles, true>&WithSnackbarProps, State> {
  state:State = {};

  render() {
    if(!this.props.userMe) {
      return (<ErrorPage msg='You need to log in to see your account details' variant='info' />);
    }
    
    const browserPushControl = this.renderBrowserPushControl();
    const androidPushControl = this.renderMobilePushControl(MobileNotificationDevice.Android);
    const iosPushControl = this.renderMobilePushControl(MobileNotificationDevice.Ios);
    const emailControl = this.renderEmailControl();
    return (
      <div className={this.props.classes.page}>
        <DividerCorner title='Notification destinations'>
          {browserPushControl && (
            <Grid container alignItems='baseline' className={this.props.classes.item}>
              <Grid item xs={12} sm={6}><Typography>Browser desktop messages</Typography></Grid>
              <Grid item xs={12} sm={6}>{browserPushControl}</Grid>
            </Grid>
          )}
          {androidPushControl && (
            <Grid container alignItems='baseline' className={this.props.classes.item}>
              <Grid item xs={12} sm={6}><Typography>Android Push messages</Typography></Grid>
              <Grid item xs={12} sm={6}>{androidPushControl}</Grid>
            </Grid>
          )}
          {iosPushControl && (
            <Grid container alignItems='baseline' className={this.props.classes.item}>
              <Grid item xs={12} sm={6}><Typography>Apple iOS Push messages</Typography></Grid>
              <Grid item xs={12} sm={6}>{iosPushControl}</Grid>
            </Grid>
          )}
          {emailControl && (
            <Grid container alignItems='baseline' className={this.props.classes.item}>
              <Grid item xs={12} sm={6}>
                <Typography>
                  Email
                  {this.props.userMe.email !== undefined && (<Typography variant='caption'>&nbsp;({this.props.userMe.email})</Typography>)}
                </Typography>
              </Grid>
              <Grid item xs={12} sm={6}>{emailControl}</Grid>
            </Grid>
          )}
        </DividerCorner>
        <DividerCorner title='Account'>
          <Grid container alignItems='baseline' className={this.props.classes.item}>
            <Grid item xs={12} sm={6}><Typography>Display name</Typography></Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                id='displayName'
                value={this.state.displayName === undefined
                  ? (this.props.userMe.name || '')
                  : (this.state.displayName || '')}
                onChange={e => this.setState({ displayName: e.target.value })}
              />
              <Button aria-label="Save" color='primary' style={{visibility:
                !this.state.displayName
                || this.state.displayName === this.props.userMe.name
                ? 'hidden' : undefined
              }} onClick={() => {
                if(!this.state.displayName
                  || !this.props.userMe
                  || this.state.displayName === this.props.userMe.name) {
                  return;
                }
                this.props.server.dispatch().userUpdate({
                  projectId: this.props.server.getProjectId(),
                  userId: this.props.userMe.userId,
                  update: { name: this.state.displayName },
                });
              }}>Save</Button>
            </Grid>
          </Grid>
          <Grid container alignItems='baseline' className={this.props.classes.item}>
            <Grid item xs={12} sm={6}><Typography>Sign out of your account</Typography></Grid>
            <Grid item xs={12} sm={6}>
              <Button onClick={() => this.props.server.dispatch().userLogout({projectId: this.props.server.getProjectId()})}
              >Sign out</Button>
            </Grid>
          </Grid>
          <Grid container alignItems='baseline' className={this.props.classes.item}>
            <Grid item xs={12} sm={6}><Typography>Delete your account</Typography></Grid>
            <Grid item xs={12} sm={6}>
              <Button
                onClick={() => this.setState({deleteDialogOpen: true})}
              >Delete</Button>
              <Dialog
                open={!!this.state.deleteDialogOpen}
                onClose={() => this.setState({deleteDialogOpen: false})}
              >
                <DialogTitle>Delete account?</DialogTitle>
                <DialogContent>
                  <DialogContentText>By deleting your account, you will be signed out of your account and your account will be permanently deleted including all of your data.</DialogContentText>
                </DialogContent>
                <DialogActions>
                  <Button onClick={() => this.setState({deleteDialogOpen: false})}>Cancel</Button>
                  <Button style={{color: this.props.theme.palette.error.main}} onClick={() => this.props.server.dispatch().userDelete({
                      projectId: this.props.server.getProjectId(),
                      userId: this.props.userMe!.userId,
                  })}>Delete</Button>
                </DialogActions>
              </Dialog>
            </Grid>
          </Grid>
        </DividerCorner>
      </div>
    );
  }

  renderBrowserPushControl() {
    if(!this.props.config || !this.props.userMe || (!this.props.config.users.onboarding.notificationMethods.browserPush && !this.props.userMe.browserPush)) {
      return;
    }

    const browserPushStatus = WebNotification.getInstance().getStatus();
    var browserPushEnabled = !!this.props.userMe.browserPush;
    var browserPushControlDisabled;
    var browserPushLabel;
    if(this.props.userMe.browserPush) {
      browserPushControlDisabled = false;
      browserPushLabel = 'Enabled';
    } else {
      switch(browserPushStatus) {
        case WebNotificationStatus.Unsupported:
          browserPushControlDisabled = true;
          browserPushLabel = 'Not supported by your current browser';
          break;
        case WebNotificationStatus.Denied:
          browserPushControlDisabled = true;
          browserPushLabel = 'You have declined access to notifications';
          break;
        default:
        case WebNotificationStatus.Available:
        case WebNotificationStatus.Granted:
          browserPushControlDisabled = false;
          browserPushLabel = 'Disabled';
          break; 
      }
    }

    return (
      <FormControlLabel
        control={(
          <Switch
            color='default'
            disabled={browserPushControlDisabled}
            checked={browserPushEnabled}
            onChange={(e, checked) => {
              if(checked) {
                WebNotification.getInstance().askPermission()
                .then(r => {
                  if(r.type === 'success') {
                    this.props.server.dispatch().userUpdate({
                      projectId: this.props.server.getProjectId(),
                      userId: this.props.userMe!.userId,
                      update: { browserPushToken: r.token },
                    });
                  } else if(r.type === 'error') {
                    if(r.userFacingMsg) {
                      this.props.enqueueSnackbar(r.userFacingMsg || 'Failed to setup browser notifications', { variant: 'error', preventDuplicate: true });
                    }
                    this.forceUpdate();
                  }
                });
              } else {
                this.props.server.dispatch().userUpdate({
                  projectId: this.props.server.getProjectId(),
                  userId: this.props.userMe!.userId,
                  update: { browserPushToken: '' },
                });
              }
            }}
          />
        )}
        label={<FormHelperText component='span' error={browserPushControlDisabled}>{browserPushLabel}</FormHelperText>}
      />
    );
  }

  renderMobilePushControl(device:MobileNotificationDevice) {
    if(!this.props.config || !this.props.userMe || (!this.props.config.users.onboarding.notificationMethods.mobilePush && (
      (device === MobileNotificationDevice.Android && !this.props.userMe.androidPush)
      || (device === MobileNotificationDevice.Ios && !this.props.userMe.iosPush)
    ))) {
      return;
    }


    const mobilePushStatus = MobileNotification.getInstance().getStatus();
    var mobilePushEnabled = false;
    var mobilePushControlDisabled;
    var mobilePushLabel;
    if((device === MobileNotificationDevice.Android && this.props.userMe.androidPush)
      || (device === MobileNotificationDevice.Ios && this.props.userMe.iosPush)) {
      mobilePushEnabled = true;
      mobilePushControlDisabled = false;
      mobilePushLabel = 'Enabled';
    } else if(MobileNotification.getInstance().getDevice() !== device) {
      mobilePushControlDisabled = true;
      mobilePushLabel = 'Not supported on current device';
    } else {
      switch(mobilePushStatus) {
        case MobileNotificationStatus.Disconnected:
          mobilePushControlDisabled = true;
          mobilePushLabel = 'Not supported on current device';
          break;
        case MobileNotificationStatus.Denied:
          mobilePushControlDisabled = true;
          mobilePushLabel = 'You have declined access to notifications';
          break;
        default:
        case MobileNotificationStatus.Available:
        case MobileNotificationStatus.Subscribed:
          mobilePushControlDisabled = false;
          mobilePushLabel = 'Supported by your browser';
          break; 
      }
    }

    return (
      <FormControlLabel
        control={(
          <Switch
            color='default'
            disabled={mobilePushControlDisabled}
            checked={mobilePushEnabled}
            onChange={(e, checked) => {
              if(checked) {
                WebNotification.getInstance().askPermission()
                .then(r => {
                  if(r.type === 'success') {
                    this.props.server.dispatch().userUpdate({
                      projectId: this.props.server.getProjectId(),
                      userId: this.props.userMe!.userId,
                      update: device === MobileNotificationDevice.Android
                        ? { androidPushToken: r.token }
                        : { iosPushToken: r.token },
                    });
                  } else if(r.type === 'error') {
                    if(r.userFacingMsg) {
                      this.props.enqueueSnackbar(r.userFacingMsg || 'Failed to setup mobile notifications', { variant: 'error', preventDuplicate: true });
                    }
                    this.forceUpdate();
                  }
                });
              } else {
                this.props.server.dispatch().userUpdate({
                  projectId: this.props.server.getProjectId(),
                  userId: this.props.userMe!.userId,
                  update: device === MobileNotificationDevice.Android
                  ? { androidPushToken: '' }
                  : { iosPushToken: '' },
                });
              }
            }}
          />
        )}
        label={<FormHelperText component='span' error={mobilePushControlDisabled}>{mobilePushLabel}</FormHelperText>}
      />
    );
  }

  renderEmailControl() {
    if(!this.props.config || !this.props.userMe || (!this.props.config.users.onboarding.notificationMethods.email && !this.props.userMe.email)) {
      return;
    }

    var enabled;
    var controlDisabled;
    var label;
    if(this.props.userMe.email) {
      controlDisabled = false;
      enabled = this.props.userMe.emailNotify;
      if(this.props.userMe.emailNotify) {
        label = 'Enabled';
      } else {
        label = 'Disabled';
      }
    } else {
      controlDisabled = true;
      enabled = false;
      label = 'No email on account';
    }

    return (
      <FormControlLabel
        control={(
          <Switch
            color='default'
            disabled={controlDisabled}
            checked={enabled}
            onChange={(e, checked) => {
              this.props.server.dispatch().userUpdate({
                projectId: this.props.server.getProjectId(),
                userId: this.props.userMe!.userId,
                update: { emailNotify: checked },
              });
            }}
          />
        )}
        label={<FormHelperText component='span' error={controlDisabled}>{label}</FormHelperText>}
      />
    );
  }
}

export default connect<ConnectProps,{},Props,ReduxState>((state, ownProps) => {
  const connectProps:ConnectProps = {
    configver: state.conf.ver, // force rerender on config change
    config: state.conf.conf,
    userMe: state.users.loggedIn.user,
  };
  return connectProps;
}, null, null, { forwardRef: true })(withStyles(styles, { withTheme: true })(withSnackbar(AccountPage)));