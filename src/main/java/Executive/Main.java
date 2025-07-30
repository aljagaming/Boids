package Executive;

public class Main {
    public static void main(String[] args) {
        Variables variables=new Variables(args);
        variables.updateExecutionStyle(ExecutionStyle.SEQUENTIAL);
    }
}
