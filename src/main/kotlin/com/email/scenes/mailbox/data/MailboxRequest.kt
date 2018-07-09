package com.email.scenes.mailbox.data

import com.email.db.models.Label
import com.email.scenes.composer.data.ComposerAttachment
import com.email.scenes.composer.data.ComposerInputData
import com.email.scenes.label_chooser.SelectedLabels

/**
 * Created by sebas on 3/20/18.
 */

sealed class MailboxRequest{
    data class GetSelectedLabels(
            val threadIds: List<String>): MailboxRequest()

    data class UpdateMailbox(
            val label: Label,
            val loadedThreadsCount: Int): MailboxRequest()

    data class UpdateEmailThreadsLabelsRelations(
            val selectedLabels: SelectedLabels,
            val selectedThreadIds: List<String>,
            val currentLabel: Label,
            val shouldRemoveCurrentLabel: Boolean
            ): MailboxRequest()

    class LinkDevice: MailboxRequest()

    data class MoveEmailThread(
            val chosenLabel: String?,
            val selectedThreadIds: List<String>,
            val currentLabel: Label
    ): MailboxRequest()

    data class LoadEmailThreads(
            val label: String,
            val loadParams: LoadParams,
            val userEmail: String
            ): MailboxRequest()

    data class SendMail(val emailId: Long,
                        val threadId: String?,
                        val data: ComposerInputData,
                        val attachments: List<ComposerAttachment>): MailboxRequest()

    class GetMenuInformation : MailboxRequest()

    data class UpdateUnreadStatus(val threadIds: List<String>,
                                  val updateUnreadStatus: Boolean,
                                  val currentLabel: Label): MailboxRequest()



}
