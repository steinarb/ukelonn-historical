import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Jobtypes from './Jobtypes';

class User extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onAccount(this.props.loginResponse.username);
        this.props.onJobtypeList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let { loginResponse, account, jobtypes, jobtypesMap, performedjob, onJobtypeFieldChange, onRegisterJob, onLogout } = this.state;
        if (loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <header>
                    <div className="container clearfix">
                        <h1 id="logo">Ukelønn for {account.firstName}</h1>
                    </div>
                </header>
                <div className="container-fluid">
                    <div className="container">
                        <div className="row border">
                            <div className="col">
                                <label>Til gode:</label>
                            </div>
                            <div className="col">
                                { account.balance }
                            </div>
                        </div>
                    </div>
                    <form className="form-horizontal" onSubmit={ e => { e.preventDefault(); }}>
                        <div className="form-group">
                            <label htmlFor="jobtype" className="col-sm-2 control-label">Velg jobb</label>
                            <div className="col-sm-10">
                                <Jobtypes id="jobtype" className="form-control" jobtypes={jobtypes} jobtypesMap={jobtypesMap} value={performedjob.transactionName} account={account} performedjob={performedjob} onJobtypeFieldChange={onJobtypeFieldChange} />
                            </div>
                        </div>
                        <div className="form-group">
                            <label htmlFor="amount" className="col-sm-2 control-label">Beløp</label>
                            <div className="col-sm-10">
                                <input id="amount" className="form-control" type="text" value={performedjob.transactionAmount} readOnly="true" /><br/>
                            </div>
                        </div>
                        <div className="form-group">
                            <div className="col-sm-offset col-sm-10">
                                <button className="btn btn-default" onClick={() => onRegisterJob(performedjob)}>Registrer jobb</button>
                            </div>
                        </div>
                    </form>
                    <div className="container">
                        <div className="row border">
                            <div className="col">
                                &nbsp;
                            </div>
                            <div className="col-xs">
                                <Link to="/ukelonn/performedjobs">Utforte jobber</Link>
                            </div>
                        </div>
                        <div className="row border">
                            <div className="col">
                                &nbsp;
                            </div>
                            <div className="col-xs">
                                <Link to="/ukelonn/performedpayments">Siste utbetalinger til bruker</Link>
                            </div>
                        </div>
                    </div>
                    <br/>
                    <br/>
                    <button onClick={() => onLogout()}>Logout</button>
                </div>
            </div>
        );
    }
};

const emptyJob = {
    account: { accountId: -1 },
    id: -1,
    transactionName: '',
    transactionAmount: 0.0
};

const mapStateToProps = state => {
    if (!state.jobtypes.find((job) => job.id === -1)) {
        state.jobtypes.unshift(emptyJob);
    }

    return {
        loginResponse: state.loginResponse,
        account: state.account,
        jobtypes: state.jobtypes,
        jobtypesMap: new Map(state.jobtypes.map(i => [i.transactionTypeName, i])),
        performedjob: state.performedjob,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
        onAccount: (username) => dispatch({ type: 'ACCOUNT_REQUEST', username }),
        onJobtypeList: () => dispatch({ type: 'JOBTYPELIST_REQUEST' }),
        onJobtypeFieldChange: (selectedValue, jobtypesMap, account, performedjob) => {
            let jobtype = jobtypesMap.get(selectedValue);
            let changedField = {
                performedjob: {
                    transactionTypeId: jobtype.id,
                    transactionName: jobtype.transactionName,
                    transactionAmount: jobtype.transactionAmount,
                    account: account
                }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onRegisterJob: (performedjob) => dispatch({ type: 'REGISTERJOB_REQUEST', performedjob: performedjob }),
    };
};

User = connect(mapStateToProps, mapDispatchToProps)(User);

export default User;
