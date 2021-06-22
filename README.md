# MidProgressLoader
Orignate progress from center to left and right

### Create xml layout
```XML
<com.ui.widget.MidProgressLoader
        android:id="@+id/linearProgressBar"
        android:layout_width="match_parent"
        android:layout_height="10dp"
        android:layout_gravity="center|bottom"
        android:visibility="gone"
        app:midProgressBgColor="@color/progress_bg"
        app:midProgressbarColor="@color/progress_bar"
        app:midStrokeCap="round"
        app:midStrokeWidth="4dp"
        tools:visibility="visible" />
```

### Load pregress in Activity or Fragment
```Kotlin
MidProgressLoader linearProgressBar
....

fun load(){
  linearProgressBar.setVisibility(View.VISIBLE)
  linearProgressBar.setProgress(0f)
  linearProgressBar.setProgressWithAnimation(100f, ValueAnimator.INFINITE, null)
}
```
