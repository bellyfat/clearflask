import React, { Component } from 'react';
import { Fade, Grow, Slide, Zoom } from '@material-ui/core';
import { withStyles, Theme, createStyles, WithStyles } from '@material-ui/core/styles';
import Loading from './Loading';
import * as Client from '../../api/client';
import { connect } from 'react-redux';
import { ReduxState } from '../../api/server';
import Message from '../comps/Message';

const styles = (theme:Theme) => createStyles({
  container: {
    margin: theme.spacing(1),
    display: 'flex',
  },
});

interface Props extends WithStyles<typeof styles, true> {
  loaded:boolean;
  error?:string;
}

class Loader extends Component<Props> {
  render() {
    if(this.props.error) {
      return (<Message message={this.props.error} variant='error' />);
    }
    if(!this.props.loaded) {
      return (<Loading />);
    }
    return (
      <Fade in={this.props.loaded}>
        <div>
          {this.props.children}
        </div>
      </Fade>
    );
  }
}

export default withStyles(styles, { withTheme: true })(Loader);