package oaksoft.algolab.easy;

public class RemoveDupsFromArray {
    //Element order not changed
    public static int removeDuplicates(int[] nums, int target) {
        if(nums.length <=1){
            return nums.length;
        }

        int p1=0;
        for(int i=0; i<nums.length; i++){
            if(nums[i] != target){
                nums[p1] = nums[i];
                p1++;
            }
        }

        return p1;
    }

    //Elements order changed
    public static int removeElement(int[] nums, int val) {
        int i = 0;
        int j = nums.length-1;

        while(i<=j){
            if(nums[i]!=val){
                i++;
            }else{
                nums[i]=nums[j];
                j--;
            }
        }
        return j+1;
    }

    public static void main(String[] args) {

        int[] strs = new int[]{3,2,2,3};
        int[] i2 = new int[]{3,1,1,3,3};

        // int[] strs = new int[] {3,0,-2,-1,1,2};
        System.out.println(removeElement(strs, 3));
        System.out.println(removeElement(i2, 3));

    }
}
