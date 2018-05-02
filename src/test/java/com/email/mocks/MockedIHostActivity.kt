package com.email.mocks

import com.email.IHostActivity
import com.email.scenes.ActivityMessage
import com.email.scenes.params.SceneParams
import com.email.utils.UIMessage

/**
 * Created by gabriel on 3/1/18.
 */

class MockedIHostActivity: IHostActivity{

    var isFinished: Boolean = false

    override fun exitToScene(params: SceneParams, activityMessage: ActivityMessage?) {
        isFinished = true
    }

    override fun showDialog(message: UIMessage) {
    }

    override fun dismissDialog() {
    }

    override fun refreshToolbarItems() {
    }

    override fun goToScene(params: SceneParams, keep: Boolean) {
    }

    override fun finishScene() {
        isFinished = true
    }

    override fun getLocalizedString(message: UIMessage): String {
        return "test"
    }

}