


package rest.view.tabs.download;

import javafx.concurrent.Task;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import rest.controller.builtin.model.Request;
import rest.util.CommonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class DownloadTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadTask.class);

    private Request request;

    private DownloadBinding binding;

    DownloadTask(Request request, DownloadBinding binding) {
        this.request = request;
        this.binding = binding;
    }

    @Override
    protected Object call() {
        binding.setState("Downloading");
        UnirestInstance instance = null;
        try {
            instance = Unirest.spawnInstance();
            CommonUtil.unirestCfg(instance, request);
            instance.request(request.method(), request.url())
                .downloadMonitor((field, fileName, bytesWritten, totalBytes) -> {
                    binding.setPercent(bytesWritten / 1000 + "kbs/" + totalBytes / 1000 + "kbs");
                    binding.setProgress(new BigDecimal(bytesWritten / 1000).doubleValue()
                        / new BigDecimal(totalBytes / 1000).doubleValue());
                })
                .asFile(binding.getName(), StandardCopyOption.REPLACE_EXISTING)
                .ifSuccess(httpResponse -> {
                    String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    binding.setExitTime(now);
                    binding.setProgress(1);
                    binding.setState("Finished");
                })
                .ifFailure(fileHttpResponse -> {
                    fileHttpResponse.getParsingError().ifPresent(Throwable::printStackTrace);
                    String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
                    binding.setExitTime(now);
                    binding.setProgress(0);
                    binding.setState("Failed");
                });
        } catch (Throwable e) {
            LOGGER.error("download task cause exception:", e);
            binding.setExitTime(String.valueOf(LocalDateTime.now()));
            binding.setState("Failed");
        } finally {
            if (instance != null) {
                instance.shutDown(true);
            }
        }
        return null;
    }

    public Request request() {
        return request;
    }

    public DownloadTask request(Request request) {
        this.request = request;
        return this;
    }

    public DownloadBinding binding() {
        return binding;
    }

    public DownloadTask binding(DownloadBinding binding) {
        this.binding = binding;
        return this;
    }
}
