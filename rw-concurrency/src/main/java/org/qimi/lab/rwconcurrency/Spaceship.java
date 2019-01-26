package org.qimi.lab.rwconcurrency;

/**
 * 太空船可以在二维空间移动位置，可以更新和读取当前位置的并发接口
 */
interface Spaceship
{
    /**
     * 读取太空船的位置
     *
     * @param coordinates 保存读取到的xy坐标
     * @return 读取坐标尝试的次数
     */
    int readPosition(final int[] coordinates);

    /**
     * xy表示移动太空船的位置
     *
     * @param xDelta x坐标轴上移动的距离
     * @param yDelta y坐标轴上移动的距离
     * @return 写入新的坐标尝试的次数
     */
    int move(final int xDelta, final int yDelta);
}
