/*
 * Copyright 2016-2018 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.ukelonn;

import java.util.List;

import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedJob;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.TransactionType;

/**
 * This is the service exposed by the ukelonn.bundle
 * after it gets all of its injections, and activates.
 *
 * The plan is to make this interface a place to access various
 * aspects of the web application, e.g. JDBC storage.
 *
 * @author Steinar Bang
 *
 */
public interface UkelonnService {

    String getMessage();

    UkelonnDatabase getDatabase();

    LogService getLogservice();

    Account getAccount(String username);

    Account registerPerformedJob(PerformedJob job);

    List<TransactionType> getJobTypes();

    List<Transaction> getJobs(int accountId);

}
