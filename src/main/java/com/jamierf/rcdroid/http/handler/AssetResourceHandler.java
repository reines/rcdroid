package com.jamierf.rcdroid.http.handler;

import android.content.res.AssetManager;
import com.google.common.collect.ImmutableList;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.handler.AbstractResourceHandler;
import org.webbitserver.handler.FileEntry;
import org.webbitserver.handler.StaticFile;
import org.webbitserver.handler.TemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class AssetResourceHandler extends AbstractResourceHandler {

    private final AssetManager assets;

    public AssetResourceHandler(AssetManager assets, Executor ioThread, TemplateEngine templateEngine) {
        super(ioThread, templateEngine);

        this.assets = assets;
    }

    public AssetResourceHandler(AssetManager assets, Executor ioThread) {
        this(assets, ioThread, new StaticFile());
    }

    public AssetResourceHandler(AssetManager assets, TemplateEngine templateEngine) {
        this(assets, newFixedThreadPool(4), templateEngine);
    }

    public AssetResourceHandler(AssetManager assets) {
        this(assets, newFixedThreadPool(4));
    }

    @Override
    protected ResourceWorker createIOWorker(HttpRequest request, HttpResponse response, HttpControl control) {
        return new ResourceWorker(request, response, control);
    }

    protected class ResourceWorker extends AbstractResourceHandler.IOWorker {
        private InputStream resource;
        private final String pathWithoutTrailingSlash;
        private final boolean isDirectory;

        protected ResourceWorker(HttpRequest request, HttpResponse response, HttpControl control) {
            super(request.uri(), request, response, control);
            isDirectory = path.endsWith("/");
            pathWithoutTrailingSlash = withoutQuery(isDirectory ? path.substring(0, path.length() - 1) : path);
        }

        @Override
        protected boolean exists() throws IOException {
            resource = getAsset(pathWithoutTrailingSlash);
            return resource != null;
        }

        @Override
        protected boolean isDirectory() throws IOException {
            return isDirectory;
        }

        @Override
        protected byte[] fileBytes() throws IOException {
            if (resource == null || isDirectory()) {
                return null;
            } else {
                return read(resource);
            }
        }

        @Override
        protected byte[] welcomeBytes() throws IOException {
            InputStream resourceStream = getAsset(welcomeFileName);
            return resourceStream == null ? null : read(resourceStream);
        }

        @Override
        protected byte[] directoryListingBytes() throws IOException {
            final ImmutableList.Builder<FileEntry> entries = ImmutableList.builder();

            final String[] files = assets.list("./");
            for (String file : files)
                entries.add(new FileEntry(file));

            return isDirectory() ? directoryListingFormatter.formatFileListAsHtml(entries.build()) : null;
        }

        private byte[] read(InputStream content) throws IOException {
            try {
                return read(content.available(), content);
            } catch (NullPointerException happensWhenReadingDirectoryPathInJar) {
                return null;
            }
        }

        private InputStream getAsset(String assetPath) throws IOException {
            // remove the initial /
            if (assetPath.startsWith("/"))
                assetPath = assetPath.substring(1);

            try {
                return assets.open(assetPath);
            }
            catch (FileNotFoundException e) {
                return null;
            }
        }
    }
}
