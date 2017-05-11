package jcollado.pw.pimpam.controller;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import jcollado.pw.pimpam.R;
import jcollado.pw.pimpam.utils.BaseActivity;
import jcollado.pw.pimpam.utils.BaseFragment;
import jcollado.pw.pimpam.utils.Functions;
import jcollado.pw.pimpam.utils.Singleton;
import jcollado.pw.pimpam.utils.UserInfo;

public class MainActivity extends BaseActivity implements ViewComicFragment.OnFragmentInteractionListener,AddComicFragment.OnFragmentInteractionListener,  Barcode_Fragment.OnFragmentInteractionListener
        {
    public static Drawer result = null;
    Toolbar toolbar;
    private CollectionFragment collectionFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setNavigationDrawer();
        Singleton.getInstance().getFirebaseModule().setConnectionDatabase();

        collectionFragment = CollectionFragment.newInstance();
        Singleton.getInstance().getDatabase().setFragment(collectionFragment);
            toolbar.setTitle(getString(R.string.seeCollection));
            getSupportActionBar().hide();
            openFragment(collectionFragment);


    }
    public static void openDrawer(){
        result.openDrawer();
    }


    private void setNavigationDrawer(){

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
       // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnlyMainProfileImageVisible(true)
                .addProfiles(
                        new ProfileDrawerItem().withName(UserInfo.getDisplayName()).withEmail(UserInfo.getUserEmail()).withIcon(UserInfo.getProfilePictureURL())
                )

                .build();
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(getString(R.string.seeCollection)).withIcon(FontAwesome.Icon.faw_book);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(getString(R.string.addComic)).withIcon(FontAwesome.Icon.faw_plus_circle);
        //SecondaryDrawerItem barcode = new SecondaryDrawerItem().withIdentifier(4).withName("Barcode").withIcon(FontAwesome.Icon.faw_barcode);
        SecondaryDrawerItem ajustes = new SecondaryDrawerItem().withIdentifier(3).withName(getString(R.string.settings)).withIcon(FontAwesome.Icon.faw_cog);
        SecondaryDrawerItem signout = new SecondaryDrawerItem().withIdentifier(3).withName(getString(R.string.logout_button)).withIcon(FontAwesome.Icon.faw_sign_out);



        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(item1,item2, new DividerDrawerItem(),ajustes,signout)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        BaseFragment fragment;
                        if (position == 1){
                            toolbar.setTitle(getString(R.string.seeCollection));

                            getSupportActionBar().hide();
                            openFragment(collectionFragment);
                        }

                        if (position == 2){
                            fragment = AddComicFragment.newInstance();
                            getSupportActionBar().hide();
                            openFragment(fragment);


                        }

                        if (position == 3) {
                            fragment = Barcode_Fragment.newInstance();
                            openFragment(fragment);
                            getSupportActionBar().show();

                        }

                        if (position == 5){
                            fragment = SettingsFragment.newInstance();

                            openFragment(fragment);


                        }
                        if (position == 6){
                            AlertDialog.Builder logOutBuilder = Functions.getModalLogOut(MainActivity.this);
                            logOutBuilder.setPositiveButton((getString(R.string.ok)), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                Singleton.getInstance().getFirebaseModule().getmAuth().signOut();
                                    Intent i = new Intent(getApplicationContext(), AuthActivity.class);
                                    startActivity(i);
                                }
                            });
                            logOutBuilder.show();
                        }
                        return true;
                    }
                })
                .build();

    }

            @Override
            public void onFragmentInteraction(Uri uri) {

            }

            private void openFragment(BaseFragment fragment){

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment,"")
                        .commit();
                result.closeDrawer();
            }

            @Override
            public void onBackPressed() {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);

            }
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if(result != null) {
                    if(result.getContents() == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        TextView tv = (TextView) findViewById(R.id.codeTX);
                        tv.setText(result.getContents());

                        Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
            }

            public void mostrarCargando(){
                onPreStartConnection(getString(R.string.loading));
            }


        }
