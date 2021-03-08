package com.inhelp.instagram.grid.data

import com.inhelp.crop_image.main.data.Ratio
import com.inhelp.instagram.R

enum class EGrid(val title: String, val iconResId: Int, val ratio: Ratio) {
    THREE_ONE(title= "3x1", iconResId = R.drawable.ic_grid_3_1, ratio = Ratio.AspectRatio(3, 1)),
    THREE_TWO(title= "3x2", iconResId = R.drawable.ic_grid_3_2, ratio = Ratio.AspectRatio(3, 2)),
    THREE_THREE(title= "3x3", iconResId = R.drawable.ic_grid_3_3, ratio = Ratio.AspectRatio(3, 3)),
    THREE_FOUR(title= "3x4", iconResId = R.drawable.ic_grid_3_4, ratio = Ratio.AspectRatio(3, 4)),
    THREE_FIVE(title= "3x5", iconResId = R.drawable.ic_grid_3_5, ratio = Ratio.AspectRatio(3, 5)),
//    TWO_ONE(title= "2x1", iconResId = R.drawable.ic_grid_2_1, ratio = Ratio.AspectRatio(2, 1)),
//    TWO_TWO(title= "2x2", iconResId = R.drawable.ic_grid_2_2, ratio = Ratio.AspectRatio(2, 2)),
//    TWO_THREE(title= "2x3", iconResId = R.drawable.ic_grid_2_3, ratio = Ratio.AspectRatio(2, 3)),
//    TWO_FOUR(title= "2x4", iconResId = R.drawable.ic_grid_2_4, ratio = Ratio.AspectRatio(2, 4)),
//    TWO_FIVE(title= "2x5", iconResId = R.drawable.ic_grid_2_5, ratio = Ratio.AspectRatio(2, 5)),
//    ONE_TWO(title= "1x2", iconResId = R.drawable.ic_grid_1_2, ratio = Ratio.AspectRatio(1, 2)),
//    ONE_THREE(title= "1x3", iconResId = R.drawable.ic_grid_1_3, ratio = Ratio.AspectRatio(1, 3)),
//    ONE_FOUR(title= "1x4", iconResId = R.drawable.ic_grid_1_4, ratio = Ratio.AspectRatio(1, 4)),
//    ONE_FIVE(title= "1x5", iconResId = R.drawable.ic_grid_1_5, ratio = Ratio.AspectRatio(1, 5));
}