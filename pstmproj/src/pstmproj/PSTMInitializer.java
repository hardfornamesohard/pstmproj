package pstmproj;

import java.util.Arrays;
import java.util.Random;

public class PSTMInitializer {
    private Agent[] students;
    private Agent[] courses;
    private final Random random;
    public PSTMInitializer() {
        random = new Random();
    }

    public Agent[] getStudents() {
        return students;
    }

    public Agent[] getCourses() {
        return courses;
    }

    public void init() {
        initAgents1();
        initPreference();
    }
    public void initAgents1() {
        students = autoGenerateStudent(5);
        courses = autoGenerateCourse(5);

    }
    public void initAgents(){
        students = new Agent[8];
        courses = new Agent[8];
        students[0] = new Agent("s1", 1, true);
        students[1] = new Agent("s2", 1, true);
        students[2] = new Agent("s3", 1, true);
        students[3] = new Agent("s4", 2, true);
        courses[0] = new Agent("c1", 1, false);
        courses[1] = new Agent("c2", 2, false);
        courses[2] = new Agent("c3", 1, false);
        courses[3] = new Agent("c4", 2, false);

    }

    private Agent[] autoGenerateCourse(int i) {
        courses = new Agent[2*i];
        for (int j = 1; j < i; j++) {
            int capacity = random.nextInt(2) +1;
            Agent course = new Agent("c"+j, capacity, false);
            course.increaseMax();
            courses[j-1] = course;
        }
        //1212 cap
        return courses;
    }

    private Agent[] autoGenerateStudent(int i) {
        students = new Agent[2*i];
        for (int j = 1; j < i; j++) {
            int capacity = random.nextInt(2) +1;
            students[j-1] = new Agent("s"+j, capacity, true);
        }
        return students;


    }


    //	public void initAgents() {
//		students = new Agent[3];
//		courses = new Agent[3];
//		students[0] = new Agent("s1", 1, true);
//		students[1] = new Agent("s2", 2, true);
//		students[2] = new Agent("s3", 1, true);
//		//students[3] = new Agent("s4", 1);
//		courses[0] = new Agent("c1", 1, false);
//		courses[1] = new Agent("c2", 2, false);
//		courses[2] = new Agent("c3", 1, false);
//		//courses[3] = new Agent("c4", 2);
//		for(Agent course : courses) {
//			course.increaseMax();
//		}
//	}
//	public void initPreference()
//	{
//		//Agent [][] pre1 = {{courses[0]}, {courses[3]}, {courses[2]}, {courses[1]}};
//		Agent [][] pre1 = {{courses[0], courses[1]}};
//		students[0].addPre(pre1);
//		//Agent [][] pre2 = {{courses[0]}, {courses[2]}, {courses[3]}, {courses[1]}};
//		Agent [][] pre2 = {{courses[0]}, {courses[1]}, {courses[2]}};
//		students[1].addPre(pre2);
//		//Agent [][] pre3 = {{courses[3]}, {courses[0]}, {courses[2],courses[0]}, {courses[1]}};
//		Agent [][] pre3 = {{courses[1]}};
//		students[2].addPre(pre3);
//		//Agent [][] pre7 = {{courses[0]}, {courses[2]}, {courses[1]}, {courses[3]}};
//		//students[3].addPre(pre7);
//		//Agent [][] pre4 = {{students[3]}, {students[0]}, {students[2]}, {students[1]}};
//		Agent [][] pre4 = {{students[0], students[1]}};
//		courses[0].addPre(pre4);
//		//Agent [][] pre5 = {{students[0]}, {students[3]}, {students[2]}, {students[1]}};
//		Agent [][] pre5 = {{students[0]}, {students[1]}, {students[2]}};
//		courses[1].addPre(pre5);
//		//Agent [][] pre6 = {{students[0]}, {students[2]}, {students[3]}, {students[1]}};
//		Agent [][] pre6 = {{students[1]}};
//		courses[2].addPre(pre6);
//		//Agent [][] pre8 = {{students[0]}, {students[2]}, {students[1]}, {students[3]}};
//		//courses[3].addPre(pre8);
//	}

    public void initPreferenceRandomly(){
        Arrays.stream(students)
                .forEach(student-> student.initPreferenceRandomly(courses, random));
        Arrays.stream(courses)
                .forEach(course-> course.initPreferenceRandomly(students, random));
    }
    public void initPreference()
    {
        //Agent [][] pre1 = {{courses[0]}, {courses[3]}, {courses[2]}, {courses[1]}};
        Agent [][] pre1 = {{courses[0], courses[1]}};
        students[0].addPre(pre1);
        //Agent [][] pre2 = {{courses[0]}, {courses[2]}, {courses[3]}, {courses[1]}};
        Agent [][] pre2 = {{courses[0]}, {courses[1]}, {courses[2]}};
        students[1].addPre(pre2);
        //Agent [][] pre3 = {{courses[3]}, {courses[0]}, {courses[2],courses[0]}, {courses[1]}};
        Agent [][] pre3 = {{courses[1]}, {courses[2]}};
        students[2].addPre(pre3);
        Agent [][] pre7 = {{courses[0]}, {courses[3]}, {courses[1]}, {courses[2]}};
        students[3].addPre(pre7);
        //Agent [][] pre4 = {{students[3]}, {students[0]}, {students[2]}, {students[1]}};
        Agent [][] pre4 = {{students[3]},{students[0], students[1]}};
        courses[0].addPre(pre4);
        //Agent [][] pre5 = {{students[0]}, {students[3]}, {students[2]}, {students[1]}};
        Agent [][] pre5 = {{students[0]}, {students[1]}, {students[2]}};
        courses[1].addPre(pre5);
        //Agent [][] pre6 = {{students[0]}, {students[2]}, {students[3]}, {students[1]}};
        Agent [][] pre6 = {{students[1]},{students[0], students[2]}};
        courses[2].addPre(pre6);

        Agent [][] pre8 = {{students[0]}, {students[2]}, {students[1]}, {students[3]}};
        courses[3].addPre(pre8);
    }
}
