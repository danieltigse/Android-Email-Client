package com.criptext.mail.scenes.emaildetail.workers

import com.criptext.mail.api.HttpClient
import com.criptext.mail.bgworker.BackgroundWorker
import com.criptext.mail.bgworker.ProgressReporter
import com.criptext.mail.db.DeliveryTypes
import com.criptext.mail.db.EmailDetailLocalDB
import com.criptext.mail.db.MailboxLocalDB
import com.criptext.mail.db.models.ActiveAccount
import com.criptext.mail.db.models.Label
import com.criptext.mail.scenes.emaildetail.data.EmailDetailAPIClient
import com.criptext.mail.scenes.emaildetail.data.EmailDetailResult
import com.criptext.mail.utils.DateUtils
import com.github.kittinunf.result.Result
import org.json.JSONObject

/**
 * Created by danieltigse on 4/18/18.
 */

class UpdateUnreadStatusWorker(
        private val db: EmailDetailLocalDB,
        private val threadId: String,
        private val updateUnreadStatus: Boolean,
        private val currentLabel: Label,
        override val publishFn: (EmailDetailResult.UpdateUnreadStatus) -> Unit)
    : BackgroundWorker<EmailDetailResult.UpdateUnreadStatus> {

    override val canBeParallelized = false

    override fun catchException(ex: Exception): EmailDetailResult.UpdateUnreadStatus {
        return EmailDetailResult.UpdateUnreadStatus.Failure()
    }

    override fun work(reporter: ProgressReporter<EmailDetailResult.UpdateUnreadStatus>): EmailDetailResult.UpdateUnreadStatus? {
        val rejectedLabels = Label.defaultItems.rejectedLabelsByMailbox(currentLabel).map { it.id }
        val emailIds = db.getFullEmailsFromThreadId(threadId, rejectedLabels).map {
            it.email.id
        }
        db.updateUnreadStatus(emailIds, updateUnreadStatus)
        return EmailDetailResult.UpdateUnreadStatus.Success(threadId, updateUnreadStatus)
    }

    override fun cancel() {
        TODO("not implemented") //To change body of created functions use CRFile | Settings | CRFile Templates.
    }

}
