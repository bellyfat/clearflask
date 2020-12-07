import { Component } from 'react';
import { intercomLoad, intercomShutdown, intercomStart, intercomUpdate } from '../common/util/intercomUtil';

var loadedAppId: string | undefined;
var startedAppId: string | undefined;
var loggedInUserHash: string | undefined;

export interface IntercomWrapperConnectProps {
  dontUseThisComponentDirectly: true; // Use IntercomWrapperMain or IntercomWrapperCustomer
  appId?: string;
  userData?: {
    user_hash: string;
    email: string;
    [key: string]: string;
  };
}
export default class IntercomWrapper extends Component<IntercomWrapperConnectProps> {
  constructor(props) {
    super(props);

    if (props.appId) {
      intercomLoad(props.appId);
      loadedAppId = props.appId;
    }
  }

  render() {
    if (this.props.appId && loadedAppId && this.props.appId !== loadedAppId) {
      // mismatch of app id, some other instance must be managing intercom
      return null;
    }
    if (!this.props.appId) {
      if (startedAppId) {
        intercomShutdown(startedAppId);
        startedAppId = undefined;
      }
      return null;
    }
    if (!loadedAppId) {
      intercomLoad(this.props.appId);
      loadedAppId = this.props.appId;
    }

    const currentUserhash = this.props.userData?.user_hash;
    if (!startedAppId) {
      intercomStart(this.props.appId, this.props.userData);
      startedAppId = this.props.appId;
      loggedInUserHash = currentUserhash;
    } else if (loggedInUserHash !== currentUserhash) {
      intercomUpdate(this.props.appId, this.props.userData);
      loggedInUserHash = currentUserhash;
    }

    return null;
  }
}
