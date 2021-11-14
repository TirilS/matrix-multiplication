<h2>How to Run the Program:</h2>

    -javac MeasureSpeedup.java
    
    -java MeasureSpeedup

You will then be asked which two programs you want to compare. 
The first one to choose is the comparator program, the one you expect to have the slowest runtime of the two.
The next is the main program, the one you want to measure the speedup of.

After choosing the programs you will have the option to choose between two file formats:
'.tex' and '.txt'.

Choosing '.tex' lets the results be written to a LaTeX file as a <i>tabular</i> that can be placed in a LaTeX <i>table</i>. 
This option is useful if you want to include the results in your own LaTeX file.

Choosing '.txt' lets the program print the results to a plain text file.
This option is useful if you want a more readable output.

<h2>Folders</h2>
The outputted results will be saved as <b>"[cores]-[FirstProgram]-[SecondProgram].[format]"</b> 
in the <i>results/</i> folder, where [cores] is the number of available processors on your computer, [FirstProgram]
and [SecondProgram] are the comparator- and the main program respectively, and [format] is 'tex' or 'txt' 
depending on your choice. Be aware that running the program twice with the same variables will overwrite 
the file.

The source code can be found in <i>src/</i> .

<h2>How the Program Works</h2>
The program, <i>MeasureSpeedup</i>, takes the input from the command line,
and, for each size (100 x 100, 200 x 200, 500 x 500 and 1000 x 1000), creates two randomly generated matrices,
and runs each of the chosen matrix-multiplication-programs for the matrices.
The runtimes of the programs are measured and compared, and written to file.