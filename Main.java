public class Main {
    public static void main(String[] args) {
        try {
            GameEngine engine = new GameEngine("C:\\Users\\nasser\\Desktop\\CS311\\project\\311CSC_Project_Dataset.csv");
            engine.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

