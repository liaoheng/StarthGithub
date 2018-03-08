package me.liaoheng.starth.github.ui.base;

import android.support.v4.content.ContextCompat;
import com.flyco.systembar.SystemBarHelper;
import com.github.liaoheng.common.ui.base.CURxBaseActivity;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import me.liaoheng.starth.github.R;

/**
 * 基础通用Activity
 *
 * @author liaoheng
 */
public class BaseActivity extends CURxBaseActivity {

    protected void initSlidrStatusBar(){
        initStatusBar();
        Slidr.attach(this, new SlidrConfig.Builder().edge(true).build());
    }

    protected void initStatusBar(){
        SystemBarHelper
                .tintStatusBar(this, ContextCompat.getColor(this, R.color.colorPrimaryDark), 0);
    }
}
