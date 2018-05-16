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
        private HashMap<List<Integer>,ArrayList<Integer[]>> map;
        private HashMap<List<Integer>,ArrayList<Integer[]>> dataCache;
        private HashMap<List<Integer>,ArrayList<String>> pathsCache;


        public VM(int N, int M) {
            this.N = N;
            this.M = M;
            NXM = new int[N][M];
            MXM = new int[M][M];
            dynamicMemory = new int[N][M];
            currentRow = 0;
            map = new HashMap<>();
            dataCache = new HashMap<>();
            pathsCache = new HashMap<>();
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

        public ArrayList<int[]> next(int row){
            ArrayList<int[]> children = new ArrayList<>();
            for(int i=0; i<M; i++){
                children.add(new int[]{row-1,i});
            }
            return children;
        }

        public void addHeader(ArrayList<Integer[]> tracer, int row, int col, int cost){
            tracer.add(new Integer[]{row+1,col+1,cost}); // <HEADER
            map.put(Collections.unmodifiableList(Arrays.asList(row, col)),tracer);
        }

            // 5 3 -> real: 4 2
        public ArrayList<String> trace(int s_task, int s_vm){
            int task = s_task - 1;
            int vm = s_vm - 1;
            if(dataCache.containsKey(Arrays.asList(task, vm))){
                System.out.println("Found in cache.");
                return pathsCache.get(Arrays.asList(task, vm));
            }


            System.out.println("Building path...");
            ArrayList<Integer[]> tracer;
            tracer = map.get(Arrays.asList(task, vm));
            int end = tracer.size();
            Integer[] header = tracer.get(end-1);
            ArrayList<Integer[]> constructed = new ArrayList<>();
            while(end > 1){
                constructed = construct(new ArrayList<>(tracer.subList(0,end-2)),constructed);//TODO maybe -2
                task = task - 2;
                tracer = map.get(Arrays.asList(task, tracer.get(end-2)[1]));
                try{
                    end = tracer.size();
                }catch(Exception e){
                    end = 0;
                }
                //1. >3
            }
            //constructed.add(header);
            Collections.reverse(constructed); // > 1 4, 2 6, 3 6, 4 2 etc..


            ArrayList<String> consPath = new ArrayList<>();
            Integer[] step;
            Integer[] prev_step;
            int cost;
            int totalCost = 0;
            consPath.add("[TARGET] -> TASK:" +(header[0])+ " VM:" +(header[1])+ " COST:" +header[2]);
            step = constructed.get(0);
            cost = NXM[step[0]][step[1]];
            totalCost += cost;
            constructed.set(0,new Integer[]{(step[0]+1),-1,(step[1]+1),cost});
            consPath.add("Serve task " +(step[0]+1)+ " with VM " +(step[1]+1)+ " with cost " +cost+ ".");
            for(int i = 1; i<constructed.size(); i++){
                step = constructed.get(i);
                prev_step = constructed.get(i-1);
                cost = NXM[step[0]][step[1]] + MXM[prev_step[2] - 1][step[1]];
                totalCost += cost; //for testing
                constructed.set(i,new Integer[]{(step[0]+1),(prev_step[2]),(step[1]+1),cost});
                consPath.add("Serve task " +(step[0]+1)+ " with VM " +(step[1]+1)+ " with cost " +cost+ ".");
            }
            if(totalCost == header[2]) System.out.println("Success");
            else {System.out.println("Error while building"); return null;}

            manageCache(constructed,consPath,new Integer[]{s_task-1,s_vm-1});
            return consPath;
        }

        private void manageCache(ArrayList<Integer[]> data, ArrayList<String> str , Integer[] key){
            if(dataCache.size()>100){
                dataCache.clear();
                pathsCache.clear();
                System.out.println("Clearing cache.");
            }
            dataCache.put(Arrays.asList(key[0], key[1]),data);
            pathsCache.put(Arrays.asList(key[0], key[1]),str);
            System.out.println("Saved in cache.");
        }

        private ArrayList<Integer[]> construct(ArrayList<Integer[]> tracer, ArrayList<Integer[]> ret){
            ret.addAll(tracer);
            return ret;
        }

        public ArrayList<Integer[]> data(int task,int vm){
            if(!dataCache.containsKey(Arrays.asList(task-1, vm-1))){
                trace(task,vm);
            }
            ArrayList<Integer[]> data = dataCache.get(Arrays.asList(task-1, vm-1));
            for(Integer[] step : data){
                System.out.println("[RESPONSE] TASK:"+step[0]+" VM:"+step[2]+" COST:"+step[3]);
            }
            return data;
        }

        public Integer[] data(int task,int vm, int step){
            if(!dataCache.containsKey(Arrays.asList(task-1, vm-1))){
                trace(task,vm);
            }
            Integer[] data = dataCache.get(Arrays.asList(task-1, vm-1)).get(step-1);
            System.out.println("[RESPONSE] STEP:"+step+" TASK:"+data[0]+" FROM_VM:"+data[1]+" VM:"+data[2]+" COST:"+data[3]);
            return data;
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
        cost = dfs.start(); //<- 0 = normal, will only print result.  1 = detailed, will print result and track tasks
        vm.trace(2,3);
        //vm.trace(1,1);
        //vm.trace(1,1);
        vm.data(2,3);
        vm.data(3,3,2);
        lEndTime = System.nanoTime();
        output = lEndTime - lStartTime;
        total += output;
        System.out.println("DFS: " + output / 1000000000f);

        System.out.println("Total: " + total / 1000000000f);
    }

    public static class DFS {
        private VM vm;
        private int cost;
        private int targetRow;
        private int[][] result;
        private ArrayList<Integer[]> tracer;
        private ArrayList<Integer[]> m_tracer;
        private int sTask;

        public DFS(VM vm){
            this.vm = vm;
            this.result = new int[vm.N][vm.M];
        }

        public int[][] start(){
            for(int row=0; row<vm.N; row++){
                for(int col=0; col<vm.M; col++){
                    dfs(row,col);
                    System.out.print(result[row][col] + " ");
                }
                System.out.println();
            }
            return result;
        }

        public int dfs(int sRow, int sCol){
            this.cost = 9999;
            this.sTask = sRow;
            if(sRow >=2){
                this.targetRow = sRow - 2; // 2 level down
            }else{
                this.targetRow = 0;
            }
            this.tracer = new ArrayList<>();
            traverse(sRow,sCol,sCol,0,tracer);
            vm.addHeader(this.m_tracer,sRow,sCol,this.cost);
            vm.insertToMemory(sRow,sCol,this.cost);
            result[sRow][sCol] = this.cost;
            return this.cost;
        }

        private void traverse(int cRow, int cCol , int fCol, int cost, ArrayList<Integer[]> tracer){

            if(cRow == this.targetRow){
                if(sTask>=2){
                    cost += heuristicMemory(cRow,cCol, fCol);
                }else{
                    cost += heuristic(cRow,cCol, fCol);
                }
                tracer.add(new Integer[]{cRow,cCol});
                markTracer(tracer,cost);
                return;
            }
            cost += heuristic(cRow, cCol, fCol);
            tracer.add(new Integer[]{cRow,cCol});
            if(cost> this.cost){ //cut-off/pruning
                return;
            }
            for(int[] col : vm.next(cRow)){ //< col[] has const row and variables columns
                traverse(cRow-1,col[1],cCol,cost,new ArrayList<>(tracer));
            }
            return;
        }

        private void markTracer(ArrayList<Integer[]> tracer,int cost){
            if(cost< this.cost){
                this.cost = cost;
                this.m_tracer = new ArrayList<>(tracer);
            }
        }

        private int heuristic(int toR, int toC, int fromC){
            int taskCost = vm.NXM[toR][toC];
            int travelCost = vm.MXM[fromC][toC];
            return taskCost + travelCost;
        }

        private int heuristicMemory(int toR, int toC, int fromC){
            int taskCost = vm.takeFromMemory(toR,toC);
            int travelCost = vm.MXM[fromC][toC];
            return taskCost + travelCost;
        }

    }

}
