package com.email.db.seeders

import com.email.db.ContactTypes
import com.email.db.dao.EmailContactJoinDao
import com.email.db.models.EmailContact

/**
 * Created by sebas on 2/7/18.
 */

class EmailContactSeeder {

    companion object {
        var emailContacts : List<EmailContact> = mutableListOf()

        fun seed(emailContactJoinDao: EmailContactJoinDao){
            emailContacts = emailContactJoinDao.getAll()
            emailContactJoinDao.deleteAll(emailContacts)
            emailContacts = mutableListOf()
            for (a in 1..4){
                emailContacts += fillEmailContacts(a)
            }
            emailContactJoinDao.insertAll(emailContacts)
        }


        private fun fillEmailContacts(iteration: Int): EmailContact {
            lateinit var emailContact: EmailContact
            when (iteration) {
                1 -> emailContact = EmailContact(
                        emailId = 1,
                        contactId = 1,
                        type = ContactTypes.FROM)
                2 -> emailContact = EmailContact( emailId = 2,
                        contactId = 2,
                        type = ContactTypes.BCC)

                3 -> emailContact = EmailContact( emailId = 1,
                        contactId = 3,
                        type = ContactTypes.CC)

                4 -> emailContact = EmailContact(
                        emailId = 3,
                        contactId = 4,
                        type = ContactTypes.FROM)
            }
            return emailContact
        }
    }
}