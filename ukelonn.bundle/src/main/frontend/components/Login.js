import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import LoginErrorMessage from './LoginErrorMessage';

let Login = ({username, password, loginResponse, onFieldChange, onLogin}) => {
    if (loginResponse.roles.length > 0) {
        if (loginResponse.roles[0] === 'administrator') {
            return (<Redirect to="/ukelonn/admin" />);
        }

        return (<Redirect to="/ukelonn/user" />);
    }

    return (
        <div className="Login">
            <h1>Ukelønn login</h1>
            <form className="form-horizontal" onSubmit={ e => { e.preventDefault(); }}>
                <div className="form-group">
                    <label htmlFor="username" className="col-sm-2 control-label">Brukernavn:</label>
                    <div className="col-sm-10">
                        <input id="username" className="form-control" type='text' name='username' onChange={(event) => onFieldChange({ username: event.target.value })}></input>
                    </div>
                </div>
                <div className="form-group">
                    <label htmlFor="password" className="col-sm-2 control-label">Passord:</label>
                    <div className="col-sm-10">
                        <input id="password" className="form-control" type='password' name='password' onChange={(event) => onFieldChange({ password: event.target.value })}/>
                    </div>
                </div>
                <div className="form-group">
                    <div className="col-sm-offset col-sm-10">
                        <button className="btn btn-default" onClick={() => onLogin(username, password)}>Login</button>
                    </div>
                </div>
            </form>
            <div className="container">
                <div className="row">
                    <LoginErrorMessage loginResponse={loginResponse} />
                </div>
            </div>

        </div>
    );
};

const mapStateToProps = state => {
    return {
        username: state.username,
        password: state.password,
        loginResponse: state.loginResponse
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onFieldChange: (changedField) => dispatch({ type: 'UPDATE', data: changedField }),
        onLogin: (username, password) => dispatch({ type: 'LOGIN_REQUEST', username, password }),
    };
};

Login = connect(mapStateToProps, mapDispatchToProps)(Login);

export default Login;
