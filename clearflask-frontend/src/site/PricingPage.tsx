import React, { Component } from 'react';
import { Typography, Grid, Button, Container, Card, CardHeader, CardContent, CardActions, Table, TableHead, TableRow, TableCell, TableBody, Paper, FormControlLabel, Switch, FormHelperText } from '@material-ui/core';
import { withStyles, Theme, createStyles, WithStyles } from '@material-ui/core/styles';
import CheckIcon from '@material-ui/icons/CheckRounded';
import HelpPopover from '../common/HelpPopover';
import { useTheme } from '@material-ui/core/styles';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import { History, Location } from 'history';
import { PRE_SELECTED_PLAN_NAME, PRE_SELECTED_BILLING_PERIOD_IS_YEARLY } from './SignupPage';

/**
 * TODO:
 * - Add yearly pricing
 * - show credits for future development (get credits for yearly in full year)
 * - Add high user limit (5k active users), add new tier with a contact button
 */
type Tier = {
  price: (isYearly:boolean) => number|'contact',
  title: string,
  description: (isYearly:boolean) => string[],
};
export const Tiers:Tier[] = [
  {
    title: 'Basic',
    price: (isYearly) => isYearly ? 50 : 80,
    description: (isYearly) => [
      'Unlimited users',
      'Simple user voting',
      `${isYearly ? '1 hour' : '5 min'} feature credits`,
    ],
  },
  {
    title: 'Pro',
    price: (isYearly) => isYearly ? 300 : 450,
    description: (isYearly) => [
      'Content analytics and search',
      'Crowd-funding',
      'Unlimited projects, users',
      `${isYearly ? '10 hours' : '1 hour'} feature credits`,
    ],
  },
  {
    title: 'Enterprise',
    price: (isYearly) => 'contact',
    description: (isYearly) => [
      'Multi-Agent Access',
      'Whitelabel',
      'Integrations, API Access',
      'Dedicated/Onsite hosting',
      'Custom SLA',
    ],
  },
];

const styles = (theme:Theme) => createStyles({
  page: {
    margin: theme.spacing(2),
  },
  option: {
    display: 'inline-block',
    margin: theme.spacing(6),
    padding: theme.spacing(6),
  },
  cardHeader: {
    // backgroundColor: theme.palette.grey[200],
  },
  cardPricing: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'baseline',
    marginBottom: theme.spacing(2),
  },
});

const T = true;
const F = false;

interface Props {
  history:History;
}
interface State {
  isYearly:boolean;
}

class LandingPage extends Component<Props&WithStyles<typeof styles, true>, State> {
  state:State = {
    isYearly: true
  };
  render() {
    return (
      <div className={this.props.classes.page}>
        <Container maxWidth='md'>
          <Typography component="h1" variant="h2" color="textPrimary">Plans and pricing</Typography>
          <Typography component="h2" variant="h4" color="textSecondary">All plans include unlimited number of users.</Typography>
          <FormControlLabel
            control={(
              <Switch
                checked={this.state.isYearly}
                onChange={(e, checked) => this.setState({isYearly: !this.state.isYearly})}
                color='default'
              />
            )}
            label={(<FormHelperText component='span'>{this.state.isYearly ? 'Yearly billing' : 'Monthly billing'}</FormHelperText>)}
          />
        </Container>
        <Container maxWidth='md'>
          <Grid container spacing={5} alignItems='stretch'>
            {Tiers.map((tier, index) => (
              <Grid item key={tier.title} xs={12} sm={index === 2 ? 12 : 6} md={4}>
                <Card raised>
                  <CardHeader
                    title={tier.title}
                    titleTypographyProps={{ align: 'center' }}
                    subheaderTypographyProps={{ align: 'center' }}
                    className={this.props.classes.cardHeader}
                  />
                  <CardContent>
                    <div className={this.props.classes.cardPricing}>
                      {tier.price(this.state.isYearly) !== 'contact' ? (
                        <React.Fragment>
                          <Typography component="h2" variant="h3" color="textPrimary">{tier.price(this.state.isYearly)}</Typography>
                          <Typography variant="h6" color="textSecondary">/month</Typography>
                        </React.Fragment>
                      ) : (
                        <Typography component="h2" variant="h4" color="textPrimary">Contact us</Typography>
                      )}
                    </div>
                    {tier.description(this.state.isYearly).map(line => (
                      <div style={{display: 'flex', alignItems: 'center'}}>
                        <CheckIcon fontSize='inherit' />
                        &nbsp;
                        <Typography variant="subtitle1" key={line}>
                          {line}
                        </Typography>
                      </div>
                    ))}
                  </CardContent>
                  <CardActions>
                    <Button fullWidth variant='text' color="primary"
                      onClick={() => tier.price(this.state.isYearly) === 'contact'
                        ? this.props.history.push('/contact')
                        : this.props.history.push('/signup', {
                          [PRE_SELECTED_PLAN_NAME]: tier.title,
                          [PRE_SELECTED_BILLING_PERIOD_IS_YEARLY]: this.state.isYearly,})
                    }>
                      {tier.price(this.state.isYearly) === 'contact' ? 'Contact us' : 'Get started'}
                    </Button>
                  </CardActions>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Container>
        <br />
        <br />
        <br />
        <Container maxWidth='md'>
          <FeatureList name='Features' planNames={['Starter', 'Pro', 'Whatever']}>
            <FeatureListItem planContents={['1','Unlimited','Unlimited']} name='Projects' />
            <FeatureListItem planContents={['Unlimited','Unlimited','Unlimited']} name='Active users' />
            <FeatureListItem planContents={['Unlimited','Unlimited','Unlimited']} name='User submitted content' />
            <FeatureListItem planContents={[T,T,T]} name='Customizable pages: Ideas, Roadmap, FAQ, Knowledge base, etc...' />
            <FeatureListItem planContents={[T,T,T]} name='Voting and Emoji expressions' />
            <FeatureListItem planContents={[F,T,T]} name='Credit system / Crowd-funding' />
            <FeatureListItem planContents={[F,T,T]} name='Analytics' />
            <FeatureListItem planContents={[F,F,T]} name='Multi agent access' />
            <FeatureListItem planContents={[F,F,T]} name='Integrations' />
            <FeatureListItem planContents={[F,F,T]} name='API access' />
            <FeatureListItem planContents={[F,F,T]} name='Whitelabel' />
          </FeatureList>
        </Container>
      </div>
    );
  }
}

const FeatureList = (props:{
  planNames:string[],
  name:string,
  children?:any,
}) => {
  const theme = useTheme();
  const mdUp = useMediaQuery(theme.breakpoints.up('sm'));
  return (
    <Paper elevation={8}>
      <Table
        size={mdUp ? 'medium' : 'small'}
      >
        <TableHead>
          <TableRow>
            <TableCell key='feature'><Typography variant='h6'>{props.name}</Typography></TableCell>
            <TableCell key='plan1'>Starter</TableCell>
            <TableCell key='plan1'>Full</TableCell>
            <TableCell key='plan1'>Enterprise</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {props.children}
        </TableBody>
      </Table>
    </Paper>
  );
}

const FeatureListItem = (props:{
  planContents:(boolean|React.ReactNode|string)[],
  name:string,
  helpText?:string
}) => {
  return (
    <TableRow key='name'>
      <TableCell key='feature'>
        {props.name}
        {props.helpText && (<HelpPopover description={props.helpText} />)}
      </TableCell>
      {props.planContents.map(content => (
        <TableCell key='plan1'>
          {content === T
            ? (<CheckIcon fontSize='inherit' />)
            : content}
        </TableCell>
      ))}
    </TableRow>
  );
}

export default withStyles(styles, { withTheme: true })(LandingPage);