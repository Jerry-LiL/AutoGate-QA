package qa.autogate.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 1;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            System.out.println("[RETRY] Retrying test: " + result.getName() + " (Attempt " + (retryCount + 1) + ")");
            return true;
        }
        return false;
    }
}
