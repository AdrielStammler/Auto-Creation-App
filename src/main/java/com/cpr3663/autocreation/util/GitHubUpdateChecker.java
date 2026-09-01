package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.controllers.AboutController;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubUpdateChecker {

    private static final String API_URL_TEMPLATE = "https://api.github.com/repos/%s/%s/releases/latest";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final String owner;
    private final String repo;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    private volatile String latestReleaseUrl;

    public GitHubUpdateChecker() {
        this.owner = "AdrielStammler";
        this.repo = "Auto-Creation-App";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    public AboutController.UpdateResult checkForUpdates() throws Exception {
        String url = String.format(API_URL_TEMPLATE, owner, repo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(TIMEOUT)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            // No releases published yet.
            return AboutController.UpdateResult.upToDate();
        }
        if (response.statusCode() != 200) {
            throw new IOException("GitHub API returned HTTP " + response.statusCode());
        }

        GitHubRelease release = mapper.readValue(response.body(), GitHubRelease.class);
        if (release.tagName == null || release.tagName.isBlank()) {
            return AboutController.UpdateResult.upToDate();
        }

        String latestVersion = stripLeadingV(release.tagName);
        latestReleaseUrl = release.htmlUrl != null ? release.htmlUrl : Constants.Links.GITHUB + "/releases/latest";

        if (isNewer(latestVersion, Constants.App.APP_VERSION)) {
            return AboutController.UpdateResult.available(latestVersion);
        }
        return AboutController.UpdateResult.upToDate();
    }

    public String getLatestReleaseUrl() {
        return latestReleaseUrl;
    }

    private static String stripLeadingV(String tag) {
        return tag.startsWith("v") || tag.startsWith("V") ? tag.substring(1) : tag;
    }

    static boolean isNewer(String candidate, String current) {
        List<Integer> a = parseVersionParts(candidate);
        List<Integer> b = parseVersionParts(current);
        int len = Math.max(a.size(), b.size());
        for (int i = 0; i < len; i++) {
            int ai = i < a.size() ? a.get(i) : 0;
            int bi = i < b.size() ? b.get(i) : 0;
            if (ai != bi) {
                return ai > bi;
            }
        }
        return false;
    }

    private static final Pattern NUMBER = Pattern.compile("\\d+");

    private static List<Integer> parseVersionParts(String version) {
        String core = version.split("[-+]", 2)[0];
        return java.util.Arrays.stream(core.split("\\."))
                .map(part -> {
                    Matcher m = NUMBER.matcher(part);
                    return m.find() ? Integer.parseInt(m.group()) : 0;
                })
                .toList();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GitHubRelease {
        public String tagName;
        public String htmlUrl;
        public String name;
        public boolean draft;
        public boolean prerelease;

        @com.fasterxml.jackson.annotation.JsonProperty("tag_name")
        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("html_url")
        public void setHtmlUrl(String htmlUrl) {
            this.htmlUrl = htmlUrl;
        }
    }
}
