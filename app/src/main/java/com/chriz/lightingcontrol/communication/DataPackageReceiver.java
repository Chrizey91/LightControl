package com.chriz.lightingcontrol.communication;

class DataPackageReceiver extends DataPackage {
    private Communicator.OnReceiveAnswerListener mAnswerListener;

    DataPackageReceiver(Communicator.OnReceiveAnswerListener answerListener) {
        this.mAnswerListener = answerListener;
    }

    Communicator.OnReceiveAnswerListener getAnswerListener() {
        return mAnswerListener;
    }
}
