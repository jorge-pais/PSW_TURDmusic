package com.turdmusic.mainApp.core.models;


import java.util.List;

/* Class with music metadata retrieved from AcousticID */
public class MusicInfo {
    private String status;
    private List<Result> results;

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
        // Substituir por uma classe Record
        private List<Record> recordings;
        private float score;

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
            private List<Artist> artists;
            private String title;

            @Override
            public String toString() {
                return "Record{" +
                        "artists=" + artists +
                        ", title='" + title + '\'' +
                        '}';
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
                private String name;

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
        }
    }
}
