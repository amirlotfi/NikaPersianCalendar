package ir.nikatech.persiancalendar.view.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import co.ronash.pushe.Pushe;
import ir.nikatech.persiancalendar.Constants;
import ir.nikatech.persiancalendar.R;
import ir.nikatech.persiancalendar.adapter.DrawerAdapter;
import ir.nikatech.persiancalendar.reminder.ReminderMainFragment;
import ir.nikatech.persiancalendar.service.ApplicationService;
import ir.nikatech.persiancalendar.service.RefreshService;
import ir.nikatech.persiancalendar.util.UpdateUtils;
import ir.nikatech.persiancalendar.util.Utils;
import ir.nikatech.persiancalendar.view.MyDrawerLayout;
import ir.nikatech.persiancalendar.view.fragment.ApplicationPreferenceFragment;
import ir.nikatech.persiancalendar.view.fragment.CalendarFragment;
import ir.nikatech.persiancalendar.view.fragment.CompassFragment;
import ir.nikatech.persiancalendar.view.fragment.ConverterFragment;

public class Main extends AppCompatActivity {

    private final String TAG = Main.class.getName();
    private Utils utils;
    private UpdateUtils updateUtils;

    private MyDrawerLayout drawerLayout;
    private DrawerAdapter adapter;

    private Class<?>[] fragments = {
            null,
            CalendarFragment.class,
            ConverterFragment.class,
            CompassFragment.class,
            ReminderMainFragment.class,
            ApplicationPreferenceFragment.class,
            null,
            null,
    };

    private static final int CALENDAR = 1;
    private static final int PREFERENCE = 5;
    private static final int EXIT = 8;

    private static final int DEFAULT = CALENDAR;

    private int menuPosition = 0;

    private String lastLocale;
    private String lastTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        utils = Utils.getInstance(getApplicationContext());
        utils.setTheme(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        utils.changeAppLanguage(this);
        utils.loadLanguageResource();
        lastLocale = utils.getAppLanguage();
        lastTheme = utils.getTheme();
        updateUtils = UpdateUtils.getInstance(getApplicationContext());

        if (!Utils.getInstance(this).isServiceRunning(ApplicationService.class)) {
            startService(new Intent(getBaseContext(), ApplicationService.class));
        }

        updateUtils.update(true);

        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else {
            toolbar.setPadding(0, 0, 0, 0);
        }

        RecyclerView navigation = (RecyclerView) findViewById(R.id.navigation_view);
        navigation.setHasFixedSize(true);
        adapter = new DrawerAdapter(this);
        navigation.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        navigation.setLayoutManager(layoutManager);

        drawerLayout = (MyDrawerLayout) findViewById(R.id.drawer);
        final View appMainView = findViewById(R.id.app_main_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            int slidingDirection = +1;

            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (isRTL())
                        slidingDirection = -1;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    slidingAnimation(drawerView, slideOffset);
                }
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            private void slidingAnimation(View drawerView, float slideOffset) {
                appMainView.setTranslationX(slideOffset * drawerView.getWidth() * slidingDirection);
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        selectItem(DEFAULT);

        LocalBroadcastManager.getInstance(this).registerReceiver(dayPassedReceiver, new IntentFilter(Constants.LOCAL_INTENT_DAY_PASSED));

        Pushe.initialize(getApplicationContext(),true);
        scheduleJob(getApplicationContext());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRTL() {
        return getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        utils.changeAppLanguage(this);
        View v = findViewById(R.id.drawer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            v.setLayoutDirection(isRTL() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
    }

    public boolean dayIsPassed = false;

    private BroadcastReceiver dayPassedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dayIsPassed = true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (dayIsPassed) {
            dayIsPassed = false;
            restartActivity();
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dayPassedReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else if (menuPosition != DEFAULT) {
            selectItem(DEFAULT);
        } else {
            SharedPreferences shared = getSharedPreferences("data", MODE_PRIVATE);
            boolean ok = shared.getBoolean("ok", true);
            if(ok) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
                builder.setMessage("از برنامه خارج میشوید؟")
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("خروج")
                        .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("برنامه های دیگر", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("bazaar://collection?slug=by_author&aid=" + "nikatech"));
                                    intent.setPackage("com.farsitel.bazaar");
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "بازار نصب نیست", Toast.LENGTH_LONG).show();
                                }
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("امتیاز به برنامه", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_EDIT);
                                    intent.setData(Uri.parse("bazaar://details?id=ir.nikatech.persiancalendar"));
                                    intent.setPackage("com.farsitel.bazaar");
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "بازار نصب نیست", Toast.LENGTH_LONG).show();
                                }
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean("ok", false);
                editor.apply();
            }else {
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Checking for the "menu" key
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void beforeMenuChange(int position) {
        if (position != menuPosition) {
            // reset app lang on menu changes, ugly hack but it seems is needed
            utils.changeAppLanguage(this);
        }

        // only if we are returning from preferences
        if (menuPosition != PREFERENCE)
            return;

        utils.updateStoredPreference();
        updateUtils.update(true);

        boolean needsActivityRestart = false;

        String locale = utils.getAppLanguage();
        if (!locale.equals(lastLocale)) {
            lastLocale = locale;
            utils.changeAppLanguage(this);
            utils.loadLanguageResource();
            needsActivityRestart = true;
        }

        if (!lastTheme.equals(utils.getTheme())) {
            needsActivityRestart = true;
            lastTheme = utils.getTheme();
        }

        if (needsActivityRestart)
            restartActivity();
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void selectItem(int item) {
        if (item == EXIT) {
            finish();
            return;
        }

        beforeMenuChange(item);
        if (menuPosition != item) {
            if (item == 6) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("bazaar://collection?slug=by_author&aid=" + "nikatech"));
                    intent.setPackage("com.farsitel.bazaar");
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "بازار نصب نیست", Toast.LENGTH_LONG).show();
                }
            } else if (item == 7) {
                try {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setData(Uri.parse("bazaar://details?id=ir.nikatech.persiancalendar"));
                    intent.setPackage("com.farsitel.bazaar");
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "بازار نصب نیست", Toast.LENGTH_LONG).show();
                }
                menuPosition = item;
            } else {
                try {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.fragment_holder,
                                    (Fragment) fragments[item].newInstance(),
                                    fragments[item].getName()
                            ).commit();
                    menuPosition = item;
                } catch (Exception e) {
                    Log.e(TAG, item + " is selected as an index", e);
                }
            }
        }

        adapter.setSelectedItem(menuPosition);

        drawerLayout.closeDrawers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.LOCATION_PERMISSION_RESULT));
        }
    }

    public static void scheduleJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job job = createJob(dispatcher);
        dispatcher.schedule(job);
    }

    public static Job createJob(FirebaseJobDispatcher dispatcher){
        Job job = dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(RefreshService.class)
                .setTag("Pushe refresh job")
                .setReplaceCurrent(false)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(120,180))
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        return job;
    }

}
