package com.sye.content.pm;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/13/2018 14:49
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public interface IPackageInstaller {

    public interface OnStartCallback {

        public void onStart();
    }

    public interface OnFinishCallback {

        public void onFinish();

    }


    public interface OnFailureCallback {

        public void onFailure(int code, String msg, Throwable thr);

    }

    public void start(Task task);

    public interface Task {


        /**
         * Start exec install callback
         *
         * @param callback
         */
        public void setOnStartCallback(OnStartCallback callback);

        /**
         * Install finish callback
         *
         * @param callback
         */
        public void setOnFinishCallback(OnFinishCallback callback);

        /**
         * Install failure callback  if need error information please set it
         *
         * @param callback
         */
        public void setOnFailureCallback(OnFailureCallback callback);


        public void setInstallMode(int mode);

    }
}
