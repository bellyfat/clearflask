import React, { Component } from 'react';
import App from '../app/App';
import {
  MemoryRouter,
  Route,
  withRouter,
  RouteComponentProps,
} from 'react-router-dom'
import { Server } from '../api/server';
import Templater from '../common/config/configTemplater';
import DataMock from '../api/dataMock';
import * as ConfigEditor from '../common/config/configEditor';
import randomUuid from '../common/util/uuid';
import ServerMock from '../api/serverMock';

export interface Project {
  server: Server;
  templater: Templater;
  editor: ConfigEditor.Editor;
}

export function getProject(
  template:((templater:Templater)=>void)|undefined = undefined,
  mock:((mocker:DataMock)=>void)|undefined = undefined,
  projectId:string = randomUuid(),
):Promise<Project> {
  const server = new Server(projectId, ServerMock.get());
  return server.dispatchAdmin()
    .then(d => d.projectCreateAdmin({projectId: projectId})
      .then(project =>{
        const editor = new ConfigEditor.EditorImpl(project.config.config);
        const templater = Templater.get(editor);
        template && template(templater);
        server.subscribeToChanges(editor);
        return d.configSetAdmin({
          projectId: projectId,
          versionLast: project.config.version,
          config: editor.getConfig(),
        })
        .then(() => mock && mock(DataMock.get(projectId)))
        .then(() => {
          if(server.getStore().getState().users.loggedIn.status === undefined) {
            server.dispatch().userBind({projectId});
          }
        })
        .then(() => server.dispatch().configGet({projectId: projectId}))
        .then(() => ({server, templater, editor}));
      })
    );
}

export function deleteProject(projectId:string) {
  ServerMock.get().deleteProject(projectId);
}

interface Props {
  server:Server;
  intialSubPath?:string;
  forcePath?:string;
}

export default class DemoApp extends Component<Props> {
  render() {
    return (
      <MemoryRouter initialEntries={[`/${this.props.server.getProjectId()}${this.props.intialSubPath || ''}`]}>
        <Route path="/:projectId" render={props => (
          <React.Fragment>
            <ForceUrl forcePath={this.props.forcePath} />
            <App
              {...props}
              supressCssBaseline
              isInsideContainer
              serverOverride={this.props.server} />
          </React.Fragment>
        )} />
      </MemoryRouter>
    );
  }
}

var lastForcedPath;
const ForceUrl = withRouter((props:RouteComponentProps&{forcePath?:string}) => {
  if(props.forcePath !== undefined
    && props.forcePath !== lastForcedPath) {
    setTimeout(() => props.history.push(`/${props.match.params['projectId']}${props.forcePath}`), 1);
  };
  lastForcedPath = props.forcePath
  return null;
});