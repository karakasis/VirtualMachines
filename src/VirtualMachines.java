//AEM:2751 ONOMA:KARAKASIS XRHSTOS EMAIL:karakasisx@gmail.com , christonk@csd.auth.gr
/*
    TODO ean paw apo level se level me HC tote xanw luseis.
    TODO afou omws kathe level mporei na sundethei me opoiodipote epomeno komvo
    TODO mporoume na upologizoume to pathing xwrizontas to dentro
    TODO se upodentra ton maximum 2 LEVEL height
    TODO etsi an upologisoume to path gia ta level -> level -> level
    TODO epiliete to provlima tou na xanw luseis
    TODO telos an sitithei to level 25 enw exei upologistei to lvl 24
    TODO tote upologize to level 24->25 epestrepse mono to lvl 25
    TODO an omws zitithei to level 26 upologise to 24-25-26 kai meta return to 26.
    TODO episis gia tin askisi autin den xreiazete na krateite to path opote tha krateiete
    TODO monaxa to kostos se ena 2d pinaka
 */
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The solution uses the divide and conquer strategy
 * Read - reads the input and converts to structure -> time complexity: irrelevant
 * Divide - split the problem into subproblems -> time complexity: O(nlogn)
 * Conquer - solve the subproblems -> time complexity: O(1)
 * Merge - merge the subsolutions -> time complexity: O(n)
 * Backtrack - fix the solution -> time complexity: O(n)
 * -> time complexity: O(nlogn)
 * @author Xrhstos Karakasis
 */
public class VirtualMachines{

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Add input argument.");
        } else {
            StringBuilder builder = new StringBuilder();
            for (String s : args) {
                builder.append(s);
            }
            new VirtualMachinesStart(builder.toString());
        }
    }

}


class VirtualMachinesStart {

    /**
     * Stores a two dimensional point in a simple structure
     * making code simpler and more readable.
     * Use point.x and point.y to retrieve values.
     * Implements the comparable class to sort values
     * accoring to x-value.
     */
    public static class VM {

        public int N;
        public int M;
        public int[][] NXM;
        public int[][] MXM;
        private int[][] dynamicMemory;
        public int currentRow;
        private ArrayList<ArrayList<ArrayList<String[]>>> pathsStr;
        private ArrayList<ArrayList<ArrayList<Integer[]>>> paths;


        public VM(int N, int M) {
            this.N = N;
            this.M = M;
            NXM = new int[N][M];
            MXM = new int[M][M];
            dynamicMemory = new int[N][M];
            currentRow = 0;
            paths  = new ArrayList<>();
            pathsStr = new ArrayList<>();
        }

        public void printNXM(){
            for(int row = 0; row < N; row ++){
                for(int col = 0; col < M; col ++){
                    System.out.print(NXM[row][col] + " ");
                }
                System.out.println();
            }
        }

        public void printMXM(){
            for(int row = 0; row < M; row ++){
                for(int col = 0; col < M; col ++){
                    System.out.print(MXM[row][col] + " ");
                }
                System.out.println();
            }
        }

        private void insertNXM(int row, int col, int target){
            NXM[row][col] = target;
        }

        private void insertMXM(int row, int col, int target){
            MXM[row][col] = target;
        }

        public void insert(int row, int col, int target, int target_array){
            if(target_array == 0){
                insertNXM(row,col,target);
            }else{
                insertMXM(row,col,target);
            }
        }

        public void insertToMemory(int row,int col, int target){
            dynamicMemory[row][col] = target;
        }

        public int takeFromMemory(int row,int col){
            return dynamicMemory[row][col]; //2 levels down
        }

        public ArrayList<int[]> getChildren(int row, int col){
            ArrayList<int[]> children = new ArrayList<>();
            for(int i=0; i<M; i++){
                children.add(new int[]{row-1,i});
            }
            return children;
        }

        public void startTracing(){
            for(int i=0; i<N; i++){
                paths.add(new ArrayList<>());
                pathsStr.add(new ArrayList<>());
                for(int j=0; j<M; j++){
                    paths.get(i).add(new ArrayList<>());
                    pathsStr.get(i).add(new ArrayList<>());
                }
            }
        }
        public void addExecutionStep(int task, int vm,int row, int col, int cost){
            paths.get(row).get(col).add(new Integer[]{task+1,vm+1,cost});
            pathsStr.get(row).get(col).add(new String[]{"Serve task "+ (task+1)
                    ," with VM "+(vm+1), " with cost "+cost+"."});
        }

        public void addHeader(ArrayList<Integer[]> tracer, int row, int col, int cost){
            paths.get(row).get(col).add(new Integer[]{row+1,col+1,cost});
            pathsStr.get(row).get(col).add(new String[]{"Target -> Task: "+(row+1)+","
                    ,"VM: "+(col+1)+",", "Total Cost: "+cost});
            for(int i=tracer.size()-1; i>=0; i--){
                addExecutionStep(tracer.get(i)[0],tracer.get(i)[1],row,col,tracer.get(i)[2]);
            }
            /*
            paths.get(row).get(col).add(new Integer[]{row+1,col+1,cost});
            Collections.reverse(paths.get(row).get(col));
            pathsStr.get(row).get(col).add(new String[]{"Target -> Task: "+(row+1)+","
                    ,"VM: "+(col+1)+",", "Total Cost: "+cost});
            Collections.reverse(pathsStr.get(row).get(col));
            */
        }

        public void trace(int task, int vm){
            //task = task - 1;

            for(String[] str : pathsStr.get(task-1).get(vm-1)){
                for(String s : str){
                    System.out.print(s);
                }
                System.out.println();
            }
            if(task - 2 > 0){
                int last_vm = paths.get(task - 1).get(vm - 1).get(1)[1];
                trace(task-2,last_vm);
            }
        }

        public void data(int task,int vm){

        }

    }

    public static class RandomMachines{

        private static int N;
        private static int M;
        private static final String FILENAME = "output.txt";
        private static int[][] NXM;
        private static int[][] MXM;

        public static void make(int v_tasks, int v_machines){
            N = v_tasks;
            M = v_machines;

            BufferedWriter bw = null;
            FileWriter fw = null;
            NXM = new int[N][M];
            MXM = new int[M][M];
            Random rand = new Random();
            try {
                File file = new File(FILENAME);
                file.createNewFile();
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write(String.valueOf(N) + "\n");
                bw.write(String.valueOf(M) + "\n");
                bw.write("\n");

                //write the N X M Array
                for(int row = 0; row < N; row++){
                    NXM[row][0] = rand.nextInt(7) + 2;
                    bw.write(String.valueOf(NXM[row][0]));
                    for(int col = 1; col < M; col++){
                        NXM[row][col] = rand.nextInt(7) + 2;
                        bw.write(" "+String.valueOf(NXM[row][col]));
                    }
                    bw.write("\n");
                }

                bw.write("\n");

                //init the M X M Array
                for(int i = 0; i < M; i++) {
                    for(int j = 0; j < i; j++) {
                        int value = rand.nextInt(7) + 2;
                        MXM[i][j] = value;
                        MXM[j][i] = value;
                    }
                }
                //write the M X M Array
                //MXM[0][0] = 0;
                /*
                bw.write(String.valueOf(MXM[0][0]));
                for(int col = 1; col < M; col++){
                    //MXM[0][col] = rand.nextInt(7) + 2;
                    bw.write(" "+String.valueOf(MXM[0][col]));
                }
                bw.write("\n");
                */
                for(int row = 0; row < M; row++){
                    //MXM[row][0] = rand.nextInt(7) + 2;
                    bw.write(String.valueOf(MXM[row][0]));
                    for(int col = 1; col < M; col++){
                        /*
                        if(row != col){
                            MXM[row][col] = rand.nextInt(7) + 2;
                        }else{
                            MXM[row][col] = 0;
                        }
                        */
                        bw.write(" "+String.valueOf(MXM[row][col]));
                    }
                    bw.write("\n");
                }

                System.out.println("Done");

            } catch (IOException e) { e.printStackTrace(); }
            finally {
                try {
                    if (bw != null) bw.close();
                    if (fw != null) fw.close();
                } catch (IOException ex) {ex.printStackTrace();}
            }
        }

    }

    /**
     * Reads input and converts to List of Points.
     *
     */
    public static class Reader {

        public Reader() {
        }

        /**
         * Reads file and converts to ArrayList<Point>.
         * In this project parameter is passed directly from the argument.
         * The file must be located at root of .jar when passed as
         * argument at runtime.
         * Implementation:
         *   Reads file into InputStream
         *   Wraps inputStream in a bufferedReader
         *   Skip the first line since its not used
         *   For every line in the input convert point and add to a list
         * Time complexity:
         *   Worst case: O(n), but
         *   calculating the complexity doesn't make much sense when individual
         *   operations have a very variable runtime behavior since they rely
         *   on I/O calls and read times of disk.
         * @param path the name of the .txt file.
         * @return ArrayList<Point> containing Points from .txt .
         */
        private VM readSample(String path){
            BufferedReader in = null;
            VM vm = null;
            StringBuilder sb = new StringBuilder(); //Instantiate once and delete in loop

            try {
                FileInputStream fis = new FileInputStream("./"+path); //read the file
                in = new BufferedReader(new InputStreamReader(fis)); //wrap it in a bufferedReader
                String str;
                int N = Integer.parseInt(in.readLine());
                int M = Integer.parseInt(in.readLine());
                vm = new VM(N,M);

                in.readLine(); // skip first new line
                //loop through every line, split the line into x and y values, parse integers and add to a list
                while ((str = in.readLine()) != null) {
                    if(str.isEmpty()) {
                        vm.currentRow = 0;
                        break; //TODO fix this with a better loop
                    }
                    try{
                        convertToValues(str,sb,vm,0);
                        //counter++;
                    }catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe_error){
                        //System.out.println("Cant parse integer from String");
                        //useful during tests but there will not be anything to catch
                        //unless the structure of input.txt changes since convert to point
                        //is optimised
                    }
                }
                while ((str = in.readLine()) != null) {
                    if(str.isEmpty()) break; //TODO fix this with a better loop
                    try{
                        convertToValues(str,sb,vm,1);
                        //counter++;
                    }catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe_error){
                        //System.out.println("Cant parse integer from String");
                        //useful during tests but there will not be anything to catch
                        //unless the structure of input.txt changes since convert to point
                        //is optimised
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "[UNSUPPORTED.ENCODING.EX]", ex);

            } catch (IOException ex) {
                Logger.getLogger(getClass()
                        .getName()).log(Level.SEVERE, "[IO.EX.ONOPEN] Filename passed: " + path
                        + " . Is the file on the same folder as the .jar ?", ex);

            } catch (Exception ex) {
                Logger.getLogger(getClass()
                        .getName()).log(Level.SEVERE, "[GENERAL.EX.ONOPEN]", ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                        return vm;

                    } catch (IOException ex) {
                        Logger.getLogger(getClass()
                                .getName()).log(Level.SEVERE, "[IO.EX.ONCLOSE]", ex);
                    }
                }
            }
            return null;

        }

        /**
         * based on http://demeranville.com/battle-of-the-tokenizers-delimited-text-parser-performance/
         * Custom splitter based on input (optimised)
         * Implementation:
         *   Start from 0 index and append char
         *   break if char is tab or whitespace (based on assignment)
         *   store x value and clear StringBuilder
         *   repeat for y value
         * This implementation is way faster than the split() of Java
         * greatly reducing runtime
         * Time complexity:
         *   Worst case: O(1), * n values -> O(n)
         * @param line the string read from the buffered Reader
         * @param sb the instance of Stringbuilder (this is to reduce instantiation calls in loop)
         * @return Point converted point
         */
        private void convertToValues(String line, StringBuilder sb, VM vm , int target_array){
            String x;
            int index = 0;
            int length = line.length();
            int outerIndex = 0;

            do{

                sb.delete(0,sb.length());
                while (index < length && line.charAt(index) != '\t' && line.charAt(index) != ' ') {
                    sb.append(line.charAt(index));
                    index++;
                }
                x = sb.toString();
                vm.insert(vm.currentRow,outerIndex++,Integer.parseInt(x),target_array);
                index++; // to skip empty space

            }while(index < length);
            vm.currentRow++;
        }

    }

    /**
     * Constructor of main class. Acts as starting point.
     *
     */
    public VirtualMachinesStart(String path){
        long lStartTime;
        long lEndTime;
        long output;
        long total = 0;

        Reader r = new Reader();

        // ----------MAKE RANDOM VMs----------
        RandomMachines.make(5,5);

        // ----------READ INPUT----------
        lStartTime = System.nanoTime();

        VM vm = r.readSample(path);
        vm.printNXM();
        System.out.println();
        vm.printMXM();

        lEndTime = System.nanoTime();
        output = lEndTime - lStartTime;
        total += output;
        System.out.println("Read: " + output / 1000000000f);

        // ----------DFS----------
        lStartTime = System.nanoTime();

        int[][] cost;
        DFS dfs = new DFS(vm);
        cost = dfs.start(1); //<- 0 = normal, will only print result.  1 = detailed, will print result and track tasks
        vm.trace(5,3);
        lEndTime = System.nanoTime();
        output = lEndTime - lStartTime;
        total += output;
        System.out.println("DFS: " + output / 1000000000f);

        System.out.println("Total: " + total / 1000000000f);
    }

    public static class DFS {
        private VM vm;
        private int cost;
        private int cCost;
        private String cTail = "";
        private int targetRow;
        private int[][] result;
        private ArrayList<Integer[]> tracer;
        private ArrayList<Integer[]> m_tracer;
        private int sTask,sVM;

        public DFS(VM vm){
            this.vm = vm;
            this.result = new int[vm.N][vm.M];
        }

        public int[][] start(int mode){
            if(mode == 0){
                if(vm.N>=2){
                    for(int row=0; row<2; row++){
                        for(int col=0; col<vm.M; col++){
                            dfsBaseline(row,col);
                            System.out.print(result[row][col] + " ");
                        }
                        System.out.println();
                    }
                }else{
                    if(vm.N == 1){
                        int row = 0;
                        for(int col=0; col<vm.M; col++){
                            dfsBaseline(row,col);
                            System.out.print(result[row][col] + " ");
                        }
                        System.out.println();
                    }else{
                        System.out.println("The amount of tasks must be at least 1.");
                    }
                }
                for(int row=2; row<vm.N; row++){
                    for(int col=0; col<vm.M; col++){
                        dfs(row,col);
                        System.out.print(result[row][col] + " ");
                    }
                    System.out.println();
                }
            }else{
                vm.startTracing();
                if(vm.N>=2){
                    for(int row=0; row<2; row++){
                        for(int col=0; col<vm.M; col++){
                            dfsDetailedBaseline(row,col);
                        }
                    }
                }else{
                    if(vm.N == 1){
                        int row = 0;
                        for(int col=0; col<vm.M; col++){
                            dfsDetailedBaseline(row,col);
                        }
                    }else{
                        System.out.println("The amount of tasks must be at least 1.");
                    }
                }
                for(int row=2; row<vm.N; row++){
                    for(int col=0; col<vm.M; col++){
                        dfsDetailed(row,col);
                    }
                }
                printResult();
            }
            return result;
        }

        public int dfs(int sRow, int sCol){
            this.cost = 9999;
            this.targetRow = sRow - 2; // 2 level down
            this.cost = traverse(sRow,sCol,sCol,0);
            vm.insertToMemory(sRow,sCol,this.cost);
            result[sRow][sCol] = this.cost;
            return this.cost;
        }

        public int dfsBaseline(int sRow, int sCol){
            this.cost = 9999;
            //no target row
            this.cost = traverseBaseline(sRow,sCol,sCol,0);
            vm.insertToMemory(sRow,sCol,this.cost);
            result[sRow][sCol] = this.cost;
            return this.cost;
        }

        //*
        public int dfsDetailed(int sRow, int sCol){
            this.cost = 9999;
            this.targetRow = sRow - 2; // 2 level down
            this.tracer = new ArrayList<>();
            this.cost = traverse(sRow,sCol,sCol,0,tracer);
            vm.addHeader(this.m_tracer,sRow,sCol,this.cost);
            vm.insertToMemory(sRow,sCol,this.cost);
            //System.out.println(this.cTail);
            result[sRow][sCol] = this.cost;
            return this.cost;
        }

        public int dfsDetailedBaseline(int sRow, int sCol){
            this.cost = 9999;
            //no target row
            this.tracer = new ArrayList<>();
            this.cost = traverseBaseline(sRow,sCol,sCol,0,tracer);
            vm.addHeader(this.m_tracer,sRow,sCol,this.cost);
            vm.insertToMemory(sRow,sCol,this.cost);
            //System.out.println(this.cTail);
            result[sRow][sCol] = this.cost;
            return this.cost;
        }
        //*/

        private int traverse(int cRow, int cCol , int fCol, int cost){

            if(cRow == this.targetRow){
                cost += heuristicTail(cRow,cCol,fCol);
                return cost;
            }
            cost += heuristic(cRow,cCol,fCol);
            if(cost> this.cost){
                return cost;
            }
            for(int[] children : vm.getChildren(cRow,cCol)){
                cCost = traverse(children[0],children[1],cCol,cost);
                if(cCost< this.cost){
                    this.cost = cCost;
                }
            }
            return this.cost;
        }

        private int traverseBaseline(int cRow, int cCol , int fCol, int cost){

            if(cRow == 0){
                cost += heuristic(cRow,cCol,fCol);
                return cost;
            }
            cost += heuristic(cRow,cCol,fCol);
            if(cost> this.cost){
                return cost;
            }
            for(int[] children : vm.getChildren(cRow,cCol)){
                cCost = traverse(children[0],children[1],cCol,cost);
                if(cCost< this.cost){
                    this.cost = cCost;
                }
            }
            return this.cost;
        }
        //*
        private int traverse(int cRow, int cCol , int fCol, int cost, ArrayList<Integer[]> tracer){

            if(cRow == this.targetRow){
                int leafcost = vm.MXM[fCol][cCol];
                int prev_cost = heuristicTail(cRow,cCol, fCol);
                cost += prev_cost;
                tracer.add(new Integer[]{cRow,cCol,leafcost});
                //vm.addExecutionStep(cRow,cCol,sTask,sVM,leafcost);
                //tail+= "Task: "+(cRow+1)+", served from VM: "+ (cCol+1) +". \nTotal Cost: "+cost+"\n";
                writePath(tracer,cost);
                return cost;
            }
            //tail += "Task: "+(cRow+1)+", served from VM: "+ (cCol+1) +". \n";
            int leafcost = heuristic(cRow, cCol, fCol);
            cost += leafcost;
            tracer.add(new Integer[]{cRow,cCol,leafcost});
            //vm.addExecutionStep(cRow,cCol,sTask,sVM,leafcost);
            if(cost> this.cost){
                return cost;
            }
            for(int[] children : vm.getChildren(cRow,cCol)){
                cCost = traverse(children[0],children[1],cCol,cost,new ArrayList<>(tracer));
                if(cCost< this.cost){
                    this.cost = cCost;
                }
            }
            return this.cost;
        }

        private int traverseBaseline(int cRow, int cCol , int fCol, int cost, ArrayList<Integer[]> tracer) {

            if (cRow == 0) {
                int leafcost = heuristic(cRow, cCol, fCol);
                cost += leafcost;
                tracer.add(new Integer[]{cRow,cCol,leafcost});
                //vm.addExecutionStep(cRow,cCol,sTask,sVM,leafcost);
                //tail+= "Task: "+(cRow+1)+", served from VM: "+ (cCol+1) +". \nTotal Cost: "+cost+"\n";
                writePath(tracer,cost);
                return cost;
            }
            //tail += "Task: "+(cRow+1)+", served from VM: "+ (cCol+1) +". \n";
            int leafcost = heuristic(cRow, cCol, fCol);
            cost += leafcost;
            tracer.add(new Integer[]{cRow,cCol,leafcost});
            //vm.addExecutionStep(cRow,cCol,sTask,sVM,leafcost);
            if (cost > this.cost) {
                return cost;
            }
            for (int[] children : vm.getChildren(cRow, cCol)) {
                cCost = traverseBaseline(children[0], children[1], cCol, cost, new ArrayList<>(tracer));
                if (cCost < this.cost) {
                    this.cost = cCost;
                }
            }
            return this.cost;
        }

        private boolean writePath(ArrayList<Integer[]> tracer,int cost){
            if(cost< this.cost){
                this.m_tracer = new ArrayList<>(tracer);
                return true;
            }else{
                return false;
            }
        }
        //*/

        private int heuristic(int toR, int toC, int fromC){
            int taskCost = vm.NXM[toR][toC];
            int travelCost = vm.MXM[fromC][toC];
            return taskCost + travelCost;
        }

        private int heuristicTail(int toR, int toC, int fromC){
            int taskCost = vm.takeFromMemory(toR,toC);
            int travelCost = vm.MXM[fromC][toC];
            return taskCost + travelCost;
        }

        public void printResult(){
            for(int row=0; row<vm.N; row++){
                for(int col=0; col<vm.M; col++){
                    System.out.print(result[row][col] + " ");
                }
                System.out.println();
            }
        }

    }

}
