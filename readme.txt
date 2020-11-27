/* *****************************************************************************
 *  Name:     Yuxi Zheng
 *  NetID:    yuxiz
 *  Precept: P07
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Operating system: Windows
 *  Compiler: IntelliJ
 *  Text editor / IDE: IntelliJ
 *
 *  Have you taken (part of) this course before: No
 *  Have you taken (part of) the Coursera course Algorithms, Part I or II: No
 *
 *  Hours to complete assignment (optional): around 3 hours excluding read me
 *   and extra credit
 *
 **************************************************************************** */

Programming Assignment 1: Percolation



/* *****************************************************************************
 *  Describe how you implemented Percolation.java. How did you check
 *  whether the system percolates?
 **************************************************************************** */
I implimented using a quick union data type uf storing connected sites and
 a boolean array storing whether a site is open or not. I updated the boolean
 array and the quick union data type every time open() is called, when also
 considering the corner cases. I included a virtual top and a virtual bottom,
 and connected all open sites in the first row to the virtual top and all
 open sites in the bottom row to the virtual bottom. I checked whether the
 system percolates by checkinig whethe the virtual top and the virtual bottom
 connect to each other.

 For the extra credit, I used another union find data type with only
 a virtual top, so anything connected to the virtual top is full.


/* *****************************************************************************
 *  Perform computational experiments to estimate the running time of
 *  PercolationStats.java for various values of n and T when implementing
 *  Percolation.java with QuickFindUF.java (not QuickUnionUF.java). Use a
 *  "doubling" hypothesis, where you successively increase either n or T by
 *  a constant multiplicative factor (not necessarily 2).
 *
 *  To do so, fill in the two tables below. Each table must have 5-10
 *  data points, ranging in time from around 0.25 seconds for the smallest
 *  data point to around 30 seconds for the largest one. Do not include
 *  data points that take less than 0.25 seconds.
 **************************************************************************** */
NOTE: All the log ratio calculated below uses the multiplicative factor
as the base.

(keep T constant)
 T = 100
 multiplicative factor (for n) = sqrt(2)

 n          time (seconds)       ratio         log ratio
--------------------------------------------------------
20        0.168
28        0.55              3.274        1.712
40        2.169             3.944        1.980
57        8.452             3.897        1.962
80        31.859            3.769        1.914


(keep n constant)
 n = 100
 multiplicative factor (for T) = 2

 T          time (seconds)       ratio         log ratio
--------------------------------------------------------
3         2.298
5         3.805              1.656         0.728
10        7.817              2.054         1.039
20        15.659             2.003         1.002
40        30.808             1.967         0.976



/* *****************************************************************************
 *  Using the empirical data from the above two tables, give a formula
 *  (using tilde notation) for the running time (in seconds) of
 *  PercolationStats.java as function of both n and T, such as
 *
 *       ~ 5.3*10^-8 * n^5.0 * T^1.5
 *
 *  Briefly explain how you determined the formula for the running time.
 *  Recall that with tilde notation, you include both the coefficient
 *  and exponents of the leading term (but not lower-order terms).
 *  Round each coefficient and exponent to two significant digits.
 **************************************************************************** */

QuickFindUF running time (in seconds) as a function of n and T:

    ~
       2.0*10^-8 * n^3.8 * T^1.0



/* *****************************************************************************
 *  Repeat the previous two questions, but using WeightedQuickUnionUF
 *  (instead of QuickFindUF).
 **************************************************************************** */

(keep T constant)
 T = 100
 multiplicative factor (for n) = sqrt(2)

 n          time (seconds)       ratio         log ratio
--------------------------------------------------------
50         0.289
71         0.545             1.886        1.830
100        1.129             2.072        2.101
141        2.206             1.954        1.933
200        4.448             2.016        2.023
283        9.631             2.165        2.229
400        19.477            2.022        2.032



(keep n constant)
 n = 100
 multiplicative factor (for T) = 2

 T          time (seconds)       ratio         log ratio
--------------------------------------------------------
50          0.578
100         1.068             1.848         0.886
200         2.223             2.081         1.058
400         4.434             1.995         0.996
800         9.57              2.158         1.110
1600        18.28             1.910         0.934



WeightedQuickUnionUF running time (in seconds) as a function of n and T:

    ~
       between 1.0*10-6 * n^2.0 * T^0.9 to  2.4*10-6 * n^2.0 * T^0.9

(When I plugged in the n and T value for both tables, they produce
slightly different values of coefficient a.)


/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */

The extra credit part took up too much memory. I tried to implement it with
array instead of an extra union find data type. But I could not get it to work.


/* *****************************************************************************
 *  Describe whatever help (if any) that you received.
 *  Don't include readings, lectures, and precepts, but do
 *  include any help from people (including course staff, lab TAs,
 *  classmates, and friends) and attribute them by name.
 **************************************************************************** */
None

/* *****************************************************************************
 *  Describe any serious problems you encountered.
 **************************************************************************** */




/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */
