package com.turdmusic.mainApp.core.models;

import java.util.List;

/** Class with music metadata retrieved from AcousticID
 * Used for interfacing with the json responses
 * */
public class MusicInfo {
    public String status;
    public List<Result> results;

    public List<Result> getResults() {
        return results;
    }
    public void setResults(List<Result> results) {
        this.results = results;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public class Result{
        public List<Record> recordings;
        public float score;

        @Override
        public String toString() {
            return "recordings=" + recordings;
        }

        public List<Record> getRecordings() {
            return recordings;
        }
        public void setRecordings(List<Record> recordings) {
            this.recordings = recordings;
        }
        public float getScore() {
            return score;
        }
        public void setScore(float score) {
            this.score = score;
        }



        public class Record{
            public List<Artist> artists;
            public String title;
            public List<ReleaseGroup> releasegroups;

            @Override
            public String toString() {
                return "Record{" +
                        "artists=" + artists +
                        ", title='" + title + '\'' +
                        ", releasegroups=" + releasegroups +
                        '}';
            }

            public List<ReleaseGroup> getReleaseGroups() {
                return releasegroups;
            }
            public void setReleaseGroups(List<ReleaseGroup> releasegroups) {
                this.releasegroups = releasegroups;
            }
            public List<Artist> getArtists() {
                return artists;
            }
            public void setArtists(List<Artist> artists) {
                this.artists = artists;
            }
            public String getTitle() {
                return title;
            }
            public void setTitle(String title) {
                this.title = title;
            }

            public class Artist{
                public String name;

                @Override
                public String toString() {
                    return name;
                }

                public String getName() {
                    return name;
                }
                public void setName(String name) {
                    this.name = name;
                }
            }

            public class ReleaseGroup{
                public String id;
                public String title;

                @Override
                public String toString() {
                    return "ReleaseGroup{" +
                            "id='" + id + '\'' +
                            ", title='" + title + '\'' +
                            '}';
                }

                public String getId() {
                    return id;
                }
                public void setId(String id) {
                    this.id = id;
                }
                public String getTitle() {
                    return title;
                }
                public void setTitle(String title) {
                    this.title = title;
                }
            }
        }
    }
}
