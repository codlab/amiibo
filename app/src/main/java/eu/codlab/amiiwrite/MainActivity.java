package eu.codlab.amiiwrite;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import eu.codlab.amiiwrite.database.controllers.AmiiboController;
import eu.codlab.amiiwrite.database.models.Amiibo;
import eu.codlab.amiiwrite.ui._stack.StackController;
import eu.codlab.amiiwrite.ui.dashboard.DashboardFragment;
import eu.codlab.amiiwrite.ui.information.fragments.AmiiboInformationFragment;
import eu.codlab.amiiwrite.ui.my_list.EventMyList;
import eu.codlab.amiiwrite.ui.my_list.fragments.MyAmiiboByCategoryFragment;
import eu.codlab.amiiwrite.ui.my_list.fragments.MyAmiiboFromCategory;
import eu.codlab.amiiwrite.ui.scan.ScanEvent;
import eu.codlab.amiiwrite.ui.scan.fragments.ScanFragment;
import eu.codlab.amiiwrite.ui.scan.fragments.ScannedAmiibo;

public class MainActivity extends AppCompatActivity implements ScanFragment.IScanListener {
    @Bind(R.id.toolbar)
    Toolbar _toolbar;

    @Bind(R.id.container)
    FrameLayout _container;

    @Bind(R.id.drawer_layout)
    DrawerLayout _drawer_layout;

    @Bind(R.id.drawer)
    View _drawer;

    private String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(),
                    Ndef.class.getName(),
            }
    };
    private StackController _stack_controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(_toolbar);

        if (_stack_controller == null) {
            _stack_controller = new StackController(this, _container);
            _stack_controller.push(new DashboardFragment());
        }

        initToolbar();
    }

    @Override
    public void onBackPressed() {
        if (!_stack_controller.pop()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<AmiiboController.AmiiboIdentifiersTuples> tuples = AmiiboController.getInstance()
                .getListOfIdentifiers();
        for (AmiiboController.AmiiboIdentifiersTuples tuple : tuples) {
            Log.d("MainActivity", tuple.identifier + " " + tuple.count);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        stopScanning();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!_stack_controller.pop()) {
                    opendDrawer();
                }
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onNewIntent(Intent paramIntent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(paramIntent.getAction())) {
            Tag tag = paramIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] uid = paramIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID);


            NfcA ntag215 = NfcA.get(tag);

            if (_stack_controller != null) {
                StackController.PopableFragment popable = _stack_controller.head();
                if (popable != null && popable instanceof ScanFragment) {
                    ((ScanFragment) popable).tryReadingAmiibo(ntag215, uid);
                }
            }
        }
    }

    private void initToolbar() {
        _toolbar.setNavigationIcon(R.drawable.ic_navigation_menu_light);
        setTitle(R.string.app_name);
        invalidateToolbar();
    }

    public void invalidateToolbar() {
        try {
            boolean has_parent = _stack_controller.hasParent();
            Log.d("MainActivity", "has_parent " + has_parent);
            if (_stack_controller.head() instanceof DashboardFragment) {
                Log.d("MainActivity", "DashboardFragment ");
                has_parent = true;
                _toolbar.setNavigationIcon(R.drawable.ic_navigation_menu_light);
            } else {
                getSupportActionBar().setHomeAsUpIndicator(0);
            }
            Log.d("MainActivity", "has_parent " + has_parent);

            getSupportActionBar().setDisplayHomeAsUpEnabled(has_parent);
            getSupportActionBar().setHomeButtonEnabled(has_parent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onScaningRequested(ScanEvent.StartFragment event) {
        _stack_controller.push(new ScanFragment());
        startScanning();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoriesRequested(EventMyList.EventLoadCategories event) {
        _stack_controller.flush();
        _stack_controller.push(new MyAmiiboByCategoryFragment());
        closeDrawwer();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onAmiibosRequested(EventMyList.EventLoadAmiibos event) {
        _stack_controller.push(MyAmiiboFromCategory.newInstance(event.identifier));
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onAmiiboRequested(EventMyList.EventLoadAmiibo event) {
        Amiibo amiibo = AmiiboController.getInstance().getAmiibo(event.id);
        _stack_controller.push(AmiiboInformationFragment.newInstance(amiibo, false));
    }

    private void opendDrawer() {
        _drawer_layout.openDrawer(_drawer);
    }

    private void closeDrawwer() {
        _drawer_layout.closeDrawer(_drawer);
    }

    public void startScanning() {
        PendingIntent localPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(PendingIntent.FLAG_NO_CREATE), 0);
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        localIntentFilter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        localIntentFilter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, localPendingIntent, new IntentFilter[]{localIntentFilter}, this.techList);
    }

    public void stopScanning() {
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }

    @Override
    public void onScanResult(byte[] bytes) {
        if (_stack_controller.head() instanceof ScanFragment) _stack_controller.pop();

        _stack_controller.push(ScannedAmiibo.newInstance(bytes));
    }
}
