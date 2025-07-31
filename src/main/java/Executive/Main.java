package Executive;

public class Main {
    public static void main(String[] args) {



        System.out.println("Hello from MPJ!");
        Variables variables=new Variables(args);
        variables.updateExecutionStyle(ExecutionStyle.SEQUENTIAL);
    }
}
