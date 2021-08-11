package rest.persitence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ch.qos.logback.core.spi.ContextAwareBase;
import rest.persitence.model.DownLoadItem;
import rest.persitence.model.RestEntity;
import rest.util.CommonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * persist and load rest entity/variable .etc
 */
public class PersistEngine extends ContextAwareBase {
    public static final String ROOT_PATH = System.getProperty("user.home") + "/.drest/";

    public static final String LOG_DIR = ROOT_PATH + ".log";

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistEngine.class);

    private static final PersistEngine PERSIST_ENGINE = new PersistEngine();

    private static final String VAR_DIR = ROOT_PATH + ".vars/";

    private static final String VAR_PATH = VAR_DIR + "variable.json";

    private static final String DOWNLOAD_DIR = ROOT_PATH + ".dln/";

    private static final String DOWNLOAD_PATH = DOWNLOAD_DIR + "dln.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Map<String, String> varCache = new HashMap<>();
    private boolean varUpdated = true;

    private PersistEngine() {
    }

    public static PersistEngine instance() {
        return PERSIST_ENGINE;
    }

    public void init() {
        LOGGER.info("begin to init work directory");
        try {
            if (Files.notExists(Paths.get(ROOT_PATH))) {
                LOGGER.info("create root dir {}" + ROOT_PATH);
                Files.createDirectory(Paths.get(ROOT_PATH));
            }
            if (Files.notExists(Paths.get(VAR_DIR))) {
                LOGGER.info("create variable dir {}", VAR_DIR);
                Files.createDirectory(Paths.get(VAR_DIR));
            }
            if (Files.notExists(Paths.get(DOWNLOAD_DIR))) {
                LOGGER.info("create download dir {}", DOWNLOAD_DIR);
                Files.createDirectory(Paths.get(DOWNLOAD_DIR));
            }
            if (Files.notExists(Paths.get(LOG_DIR))) {
                LOGGER.info("create download dir {}", LOG_DIR);
                Files.createDirectory(Paths.get(LOG_DIR));
            }
        } catch (Exception e) {
            LOGGER.error("create work directory failed", e);
        }
    }

    public List<RestEntity> loadEntities() {
        try {
            return Files.list(Paths.get(ROOT_PATH)).map(path -> {
                if (Files.isRegularFile(path) && path.toString().endsWith(".rest")) {
                    try {
                        return GSON.fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8),
                            RestEntity.class);
                    } catch (IOException e) {
                        LOGGER.error("from json str failed ", e);
                    }
                } else {
                    LOGGER.warn("skip can not recognize file {}", path);
                }
                return null;
            })
                .filter(Objects::nonNull)
                .peek(restEntity -> LOGGER.info("new rest entity loaded:{}", restEntity))
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("load entities failed ", e);
        }
        return new ArrayList<>();
    }

    public RestEntity loadExample() {
        try {
            String s =
                "{\"uuid\":\"2aae0ba1\",\"name\":\"example\",\"opened\":true,\"current\":true,\"batch\":[{\"name\":\"get\",\"basicAuth\":{\"username\":\"\",\"password\":\"\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/get\",\"header\":{\"accept\":\"application/json\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"1128\",\"response\":\"{\\n  \\\"args\\\": {},\\n  \\\"headers\\\": {\\n    \\\"Accept\\\": \\\"application/json\\\",\\n    \\\"Accept-Encoding\\\": \\\"gzip\\\",\\n    \\\"Host\\\": \\\"httpbin.org\\\",\\n    \\\"User-Agent\\\": \\\"unirest-java/3.1.00\\\",\\n    \\\"X-Amzn-Trace-Id\\\": \\\"Root\\\\u003d1-5fcf1e36-023823e6674298560b122881\\\"\\n  },\\n  \\\"origin\\\": \\\"121.37.50.40\\\",\\n  \\\"url\\\": \\\"https://httpbin.org/get\\\"\\n}\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"307\",\"Date\":\"Tue, 08 Dec 2020 06:33:26 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"application/json\"}},{\"name\":\"post\",\"basicAuth\":{\"username\":\"\",\"password\":\"\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"POST\",\"url\":\"https://httpbin.org/post\",\"header\":{\"accept\":\"application/json\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"1924\",\"response\":\"{\\n  \\\"args\\\": {},\\n  \\\"data\\\": \\\"\\\",\\n  \\\"files\\\": {},\\n  \\\"form\\\": {},\\n  \\\"headers\\\": {\\n    \\\"Accept\\\": \\\"application/json\\\",\\n    \\\"Accept-Encoding\\\": \\\"gzip\\\",\\n    \\\"Content-Length\\\": \\\"0\\\",\\n    \\\"Content-Type\\\": \\\"text/plain; charset\\\\u003dUTF-8\\\",\\n    \\\"Host\\\": \\\"httpbin.org\\\",\\n    \\\"User-Agent\\\": \\\"unirest-java/3.1.00\\\",\\n    \\\"X-Amzn-Trace-Id\\\": \\\"Root\\\\u003d1-5fcf1e37-32c93a3c1cda6c5b17c2689e\\\"\\n  },\\n  \\\"origin\\\": \\\"121.37.50.40\\\",\\n  \\\"url\\\": \\\"https://httpbin.org/post\\\"\\n}\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"449\",\"Date\":\"Tue, 08 Dec 2020 06:33:27 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"application/json\"}},{\"name\":\"basicAuth\",\"basicAuth\":{\"username\":\"auth\",\"password\":\"passwword\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/basic-auth/auth/passwword\",\"header\":{\"accept\":\"application/json\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"1819\",\"response\":\"{\\n  \\\"authenticated\\\": true,\\n  \\\"user\\\": \\\"auth\\\"\\n}\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"47\",\"Date\":\"Tue, 08 Dec 2020 06:33:27 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"application/json\"}},{\"name\":\"202\",\"basicAuth\":{\"username\":\"auth\",\"password\":\"passwword\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/status/202\",\"header\":{\"accept\":\"application/json\"},\"form\":{},\"body\":\"\",\"code\":\"202\",\"time\":\"1115\",\"response\":\"\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"0\",\"Date\":\"Tue, 08 Dec 2020 06:33:26 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"text/html; charset\\\\u003dutf-8\"}},{\"name\":\"503\",\"basicAuth\":{\"username\":\"auth\",\"password\":\"passwword\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/status/503\",\"header\":{\"accept\":\"application/json\"},\"form\":{},\"body\":\"\",\"code\":\"503\",\"time\":\"906\",\"response\":\"\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"0\",\"Date\":\"Tue, 08 Dec 2020 08:15:59 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"text/html; charset\\\\u003dutf-8\"}},{\"name\":\"404\",\"basicAuth\":{\"username\":\"auth\",\"password\":\"passwword\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/status/404\",\"header\":{\"accept\":\"application/json\"},\"form\":{},\"body\":\"\",\"code\":\"404\",\"time\":\"1125\",\"response\":\"\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"0\",\"Date\":\"Tue, 08 Dec 2020 06:33:26 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"text/html; charset\\\\u003dutf-8\"}},{\"name\":\"xml\",\"basicAuth\":{\"username\":\"auth\",\"password\":\"passwword\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/xml\",\"header\":{\"accept\":\"application/xml\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"2533\",\"response\":\"\\\\u003c?xml version\\\\u003d\\\"1.0\\\" encoding\\\\u003d\\\"UTF-8\\\"?\\\\u003e\\n\\\\u003cslideshow author\\\\u003d\\\"Yours Truly\\\" date\\\\u003d\\\"Date of publication\\\" title\\\\u003d\\\"Sample Slide Show\\\"\\\\u003e\\n  \\\\u003c!-- TITLE SLIDE --\\\\u003e\\n  \\\\u003cslide type\\\\u003d\\\"all\\\"\\\\u003e\\n    \\\\u003ctitle\\\\u003eWake up to WonderWidgets!\\\\u003c/title\\\\u003e\\n  \\\\u003c/slide\\\\u003e\\n  \\\\u003c!-- OVERVIEW --\\\\u003e\\n  \\\\u003cslide type\\\\u003d\\\"all\\\"\\\\u003e\\n    \\\\u003ctitle\\\\u003eOverview\\\\u003c/title\\\\u003e\\n    \\\\u003citem\\\\u003eWhy \\\\u003cem\\\\u003eWonderWidgets\\\\u003c/em\\\\u003e are great\\\\u003c/item\\\\u003e\\n    \\\\u003citem/\\\\u003e\\n    \\\\u003citem\\\\u003eWho \\\\u003cem\\\\u003ebuys\\\\u003c/em\\\\u003e WonderWidgets\\\\u003c/item\\\\u003e\\n  \\\\u003c/slide\\\\u003e\\n\\\\u003c/slideshow\\\\u003e\\n\\\\u003c!--  A SAMPLE set of slides  --\\\\u003e\\n\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"522\",\"Date\":\"Tue, 08 Dec 2020 06:33:26 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"application/xml\"}},{\"name\":\"json\",\"basicAuth\":{\"username\":\"auth\",\"password\":\"passwword\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/json\",\"header\":{\"accept\":\"application/xml\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"2140\",\"response\":\"{\\n  \\\"slideshow\\\": {\\n    \\\"author\\\": \\\"Yours Truly\\\",\\n    \\\"date\\\": \\\"date of publication\\\",\\n    \\\"slides\\\": [\\n      {\\n        \\\"title\\\": \\\"Wake up to WonderWidgets!\\\",\\n        \\\"type\\\": \\\"all\\\"\\n      },\\n      {\\n        \\\"items\\\": [\\n          \\\"Why \\\\u003cem\\\\u003eWonderWidgets\\\\u003c/em\\\\u003e are great\\\",\\n          \\\"Who \\\\u003cem\\\\u003ebuys\\\\u003c/em\\\\u003e WonderWidgets\\\"\\n        ],\\n        \\\"title\\\": \\\"Overview\\\",\\n        \\\"type\\\": \\\"all\\\"\\n      }\\n    ],\\n    \\\"title\\\": \\\"Sample Slide Show\\\"\\n  }\\n}\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"429\",\"Date\":\"Tue, 08 Dec 2020 06:34:02 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"application/json\"}},{\"name\":\"deflate\",\"basicAuth\":{\"username\":\"auth\",\"password\":\"passwword\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/deflate\",\"header\":{\"accept\":\"application/xml\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"1994\",\"response\":\"{\\n  \\\"deflated\\\": true, \\n  \\\"headers\\\": {\\n    \\\"Accept\\\": \\\"application/xml\\\", \\n    \\\"Accept-Encoding\\\": \\\"gzip\\\", \\n    \\\"Authorization\\\": \\\"Basic YXV0aDpwYXNzd3dvcmQ\\\\u003d\\\", \\n    \\\"Host\\\": \\\"httpbin.org\\\", \\n    \\\"User-Agent\\\": \\\"unirest-java/3.1.00\\\", \\n    \\\"X-Amzn-Trace-Id\\\": \\\"Root\\\\u003d1-5fcf1eca-78b5008e090adeb83225d1c5\\\"\\n  }, \\n  \\\"method\\\": \\\"GET\\\", \\n  \\\"origin\\\": \\\"121.37.50.40\\\"\\n}\\n\",\"respHeader\":{\"Transfer-Encoding\":\"chunked\",\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Date\":\"Tue, 08 Dec 2020 06:35:54 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"application/json\"}},{\"name\":\"html\",\"basicAuth\":{\"username\":\"auth\",\"password\":\"passwword\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/html\",\"header\":{\"accept\":\"application/xml\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"1144\",\"response\":\"\\\\u003c!DOCTYPE html\\\\u003e\\n\\\\u003chtml\\\\u003e\\n  \\\\u003chead\\\\u003e\\n  \\\\u003c/head\\\\u003e\\n  \\\\u003cbody\\\\u003e\\n      \\\\u003ch1\\\\u003eHerman Melville - Moby-Dick\\\\u003c/h1\\\\u003e\\n\\n      \\\\u003cdiv\\\\u003e\\n        \\\\u003cp\\\\u003e\\n          Availing himself of the mild, summer-cool weather that now reigned in these latitudes, and in preparation for the peculiarly active pursuits shortly to be anticipated, Perth, the begrimed, blistered old blacksmith, had not removed his portable forge to the hold again, after concluding his contributory work for Ahab\\\\u0027s leg, but still retained it on deck, fast lashed to ringbolts by the foremast; being now almost incessantly invoked by the headsmen, and harpooneers, and bowsmen to do some little job for them; altering, or repairing, or new shaping their various weapons and boat furniture. Often he would be surrounded by an eager circle, all waiting to be served; holding boat-spades, pike-heads, harpoons, and lances, and jealously watching his every sooty movement, as he toiled. Nevertheless, this old man\\\\u0027s was a patient hammer wielded by a patient arm. No murmur, no impatience, no petulance did come from him. Silent, slow, and solemn; bowing over still further his chronically broken back, he toiled away, as if toil were life itself, and the heavy beating of his hammer the heavy beating of his heart. And so it was.—Most miserable! A peculiar walk in this old man, a certain slight but painful appearing yawing in his gait, had at an early period of the voyage excited the curiosity of the mariners. And to the importunity of their persisted questionings he had finally given in; and so it came to pass that every one now knew the shameful story of his wretched fate. Belated, and not innocently, one bitter winter\\\\u0027s midnight, on the road running between two country towns, the blacksmith half-stupidly felt the deadly numbness stealing over him, and sought refuge in a leaning, dilapidated barn. The issue was, the loss of the extremities of both feet. Out of this revelation, part by part, at last came out the four acts of the gladness, and the one long, and as yet uncatastrophied fifth act of the grief of his life\\\\u0027s drama. He was an old man, who, at the age of nearly sixty, had postponedly encountered that thing in sorrow\\\\u0027s technicals called ruin. He had been an artisan of famed excellence, and with plenty to do; owned a house and garden; embraced a youthful, daughter-like, loving wife, and three blithe, ruddy children; every Sunday went to a cheerful-looking church, planted in a grove. But one night, under cover of darkness, and further concealed in a most cunning disguisement, a desperate burglar slid into his happy home, and robbed them all of everything. And darker yet to tell, the blacksmith himself did ignorantly conduct this burglar into his family\\\\u0027s heart. It was the Bottle Conjuror! Upon the opening of that fatal cork, forth flew the fiend, and shrivelled up his home. Now, for prudent, most wise, and economic reasons, the blacksmith\\\\u0027s shop was in the basement of his dwelling, but with a separate entrance to it; so that always had the young and loving healthy wife listened with no unhappy nervousness, but with vigorous pleasure, to the stout ringing of her young-armed old husband\\\\u0027s hammer; whose reverberations, muffled by passing through the floors and walls, came up to her, not unsweetly, in her nursery; and so, to stout Labor\\\\u0027s iron lullaby, the blacksmith\\\\u0027s infants were rocked to slumber. Oh, woe on woe! Oh, Death, why canst thou not sometimes be timely? Hadst thou taken this old blacksmith to thyself ere his full ruin came upon him, then had the young widow had a delicious grief, and her orphans a truly venerable, legendary sire to dream of in their after years; and all of them a care-killing competency.\\n        \\\\u003c/p\\\\u003e\\n      \\\\u003c/div\\\\u003e\\n  \\\\u003c/body\\\\u003e\\n\\\\u003c/html\\\\u003e\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"3741\",\"Date\":\"Tue, 08 Dec 2020 06:36:21 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"text/html; charset\\\\u003dutf-8\"}},{\"name\":\"delay\",\"basicAuth\":{\"username\":\"\",\"password\":\"\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/delay/6\",\"header\":{\"accept\":\"application/xml\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"6167\",\"response\":\"{\\n  \\\"args\\\": {}, \\n  \\\"data\\\": \\\"\\\", \\n  \\\"files\\\": {}, \\n  \\\"form\\\": {}, \\n  \\\"headers\\\": {\\n    \\\"Accept\\\": \\\"application/xml\\\", \\n    \\\"Accept-Encoding\\\": \\\"gzip\\\", \\n    \\\"Authorization\\\": \\\"Basic YXV0aDpwYXNzd3dvcmQ\\\\u003d\\\", \\n    \\\"Host\\\": \\\"httpbin.org\\\", \\n    \\\"User-Agent\\\": \\\"unirest-java/3.1.00\\\", \\n    \\\"X-Amzn-Trace-Id\\\": \\\"Root\\\\u003d1-5fcf1f0d-473f202a602825ea316c0280\\\"\\n  }, \\n  \\\"origin\\\": \\\"121.37.50.40\\\", \\n  \\\"url\\\": \\\"https://httpbin.org/delay/5\\\"\\n}\\n\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"408\",\"Date\":\"Tue, 08 Dec 2020 06:37:06 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"application/json\"}},{\"name\":\"cookies\",\"basicAuth\":{\"username\":\"\",\"password\":\"\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/cookies\",\"header\":{\"accept\":\"application/xml\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"2362\",\"response\":\"{\\n  \\\"cookies\\\": {}\\n}\\n\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"20\",\"Date\":\"Tue, 08 Dec 2020 06:37:45 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"application/json\"}},{\"name\":\"download\",\"basicAuth\":{\"username\":\"\",\"password\":\"\"},\"proxy\":{\"host\":\"\",\"port\":0,\"password\":\"\",\"username\":\"\"},\"verify\":false,\"method\":\"GET\",\"url\":\"https://httpbin.org/image/png\",\"header\":{\"accept\":\"application/xml\"},\"form\":{},\"body\":\"\",\"code\":\"200\",\"time\":\"1858\",\"response\":\"\",\"respHeader\":{\"Server\":\"gunicorn/19.9.0\",\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Credentials\":\"true\",\"Connection\":\"Keep-Alive\",\"Content-Length\":\"8090\",\"Date\":\"Tue, 08 Dec 2020 06:38:32 GMT\",\"via\":\"proxy A\",\"Content-Type\":\"image/png\"}}]}";
            return GSON.fromJson(s, RestEntity.class);
        } catch (Throwable e) {
            LOGGER.error("load example data error:", e);
        }
        RestEntity restEntity = new RestEntity();
        restEntity.setName("default");
        restEntity.setCurrent(true);
        restEntity.setOpened(true);
        return restEntity;
    }

    public void persistEntities(List<RestEntity> restEntities) {
        LOGGER.info("persist entity begin:{}", restEntities);
        restEntities.forEach(restEntity -> {
            try {
                Path path = Paths.get(ROOT_PATH + CommonUtil.getFileName(restEntity));
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
                String str = GSON.toJson(restEntity);
                Files.write(path, str.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                LOGGER.info("persist entity occur error", e);
            }
        });
    }

    public RestEntity loadEntity(File file) throws IOException {
        LOGGER.info("load entity from file {}", file);
        return GSON.fromJson(new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8),
            RestEntity.class);
    }

    public Map<String, String> loadVariable() {
        if (!varUpdated) {
            return varCache;
        }
        try {
            if (Files.exists(Paths.get(VAR_PATH))) {
                varCache = GSON.fromJson(new String(Files.readAllBytes(Paths.get(VAR_PATH)), StandardCharsets.UTF_8),
                    new TypeToken<Map<String, String>>() {}.getType());
            }
        } catch (IOException e) {
            LOGGER.error("load variable error ", e);
        }
        LOGGER.info("load global variable res:{}", varCache);
        varUpdated = false;
        return varCache;
    }

    public void persistVariable(Map<String, String> map) {
        LOGGER.info("persist variable data:{}", map);
        Path path = Paths.get(VAR_PATH);
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            String str = GSON.toJson(map);
            Files.write(path, str.getBytes(StandardCharsets.UTF_8));
            varUpdated = true;
        } catch (IOException e) {
            LOGGER.error("persist variable data failed ", e);
        }
    }

    public List<DownLoadItem> loadDownloadItems() {
        List<DownLoadItem> ret = new ArrayList<>();
        Path path = Paths.get(DOWNLOAD_PATH);
        try {
            if (Files.exists(path)) {
                ret = GSON.fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8),
                    new TypeToken<List<DownLoadItem>>() {}.getType());
            }

        } catch (IOException e) {
            LOGGER.error("load download items", e);
        }
        LOGGER.info("load download item end:{}", ret);
        return ret;
    }

    public void persistDownloadItems(List<DownLoadItem> items) {
        LOGGER.info("begin to persist download items:{}", items);
        Path path = Paths.get(DOWNLOAD_PATH);
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            String str = GSON.toJson(items);
            Files.write(path, str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.error("persist download items error", e);
        }
    }

    public RestEntity newEntityWithName(String s) {
        RestEntity restEntity = new RestEntity();
        restEntity.setName(s);
        persistEntities(Collections.singletonList(restEntity));
        return restEntity;
    }

    public void deleteEntityByName(String name) {
        LOGGER.info("delete rest entity:{}", name);
        Path path = Paths.get(ROOT_PATH + name + CommonUtil.ENTITY_FILE_NAME_SUFFIX);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            LOGGER.error("delete entity error:", e);
        }
    }
}
