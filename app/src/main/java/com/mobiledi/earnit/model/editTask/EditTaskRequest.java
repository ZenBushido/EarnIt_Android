package com.mobiledi.earnit.model.editTask;

public class EditTaskRequest {

    public class Children{

        private int id;

        public Children(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Children{" +
                    "id=" + id +
                    '}';
        }
    }

    public class Goal{

        private int id;

        public Goal(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Goal{" +
                    "id=" + id +
                    '}';
        }
    }


}
