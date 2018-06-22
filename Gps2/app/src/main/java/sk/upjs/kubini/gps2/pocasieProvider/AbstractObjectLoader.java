package sk.upjs.kubini.gps2.pocasieProvider;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Inspirovane podla CursorLoader (http://grepcode.com/file/repo1.maven.org/maven2/org.robolectric/android-all/4.4_r1-robolectric-1/android/content/CursorLoader.java)
 */
public abstract class AbstractObjectLoader<R> extends AsyncTaskLoader<R> {

    private R result;

    public AbstractObjectLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(R result) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (result != null) {
                onReleaseResources(result);
            }
        }
        R oldResult = this.result;
        this.result = result;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(result);
        }

        // At this point we can release the resources associated with
        // 'oldResult' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldResult != null) {
            onReleaseResources(oldResult);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (this.result != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(this.result);
        }

        if (takeContentChanged() || this.result == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(R result) {
        super.onCanceled(result);

        // At this point we can release the resources associated with 'result'
        // if needed.
        onReleaseResources(result);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'result'
        // if needed.
        if (this.result != null) {
            onReleaseResources(this.result);
            this.result = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(R result) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }

}
