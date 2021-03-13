package org.magnum.dataup.utils;

import org.magnum.dataup.model.Video;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class VideoUtils {

    private static final AtomicLong currentId = new AtomicLong(0L);
    private static final Map<Long, Video> videos = new HashMap<>();

    public static Video getById(Long id) {
        return videos.get(id);
    }

    public static Collection<Video> getVideos() {
        return videos.values();
    }

    public static Video save(Video entity) {
        checkAndSetId(entity);
        entity.setDataUrl(getDataUrl(entity.getId()));
        videos.put(entity.getId(), entity);
        return entity;
    }

    private static void checkAndSetId(Video entity) {
        if (entity.getId() == 0){
            entity.setId(currentId.incrementAndGet());
        }
    }

    private static String getDataUrl(long videoId){
        return getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
    }

    private static String getUrlBaseForLocalServer() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return "http://"+request.getServerName()
                + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
    }
}
