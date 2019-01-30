package oaksoft.algolab.easy;

public class SearchInsertPosition {
    public static int searchInsert(int[] nums, int target) {
        if(nums.length==0){
            return 0;
        }
        return bSearch(nums,0, nums.length-1, target);
    }

    private static int bSearch (int[]nums, int from, int to, int target){
        if(from==to){
            if(nums[from]<target) {
                return from+1;
            }
            if(nums[from] >= target){
                return from;
            }
        }
        int mid = (from+to)/2;
        int mv = nums[mid];
        if(target == mv){
            return mid;
        }else if(target>mv){
            return bSearch(nums, mid+1, to, target);
        } else {
            return bSearch(nums, from, mid, target);
        }
    }

    public static void main(String[] args) {

        int[] strs = new int[]{2};
        int[] i2 = new int[]{2,3,5};

        // int[] strs = new int[] {3,0,-2,-1,1,2};
        System.out.println(searchInsert(strs,4));
        System.out.println(searchInsert(i2,4));

    }
}
