package com.example.dounn.menutendina;import android.animation.Animator;import android.animation.AnimatorListenerAdapter;import android.animation.ValueAnimator;import android.content.ComponentName;import android.content.Context;import android.content.Intent;import android.content.SharedPreferences;import android.content.pm.PackageManager;import android.content.pm.ResolveInfo;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.net.Uri;import android.os.Bundle;import android.os.Environment;import android.os.Parcelable;import android.provider.MediaStore;import android.support.annotation.CallSuper;import android.support.annotation.NonNull;import android.support.design.widget.NavigationView;import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;import android.support.v4.view.GravityCompat;import android.support.v4.widget.DrawerLayout;import android.support.v4.widget.NestedScrollView;import android.support.v4.widget.SwipeRefreshLayout;import android.support.v7.app.ActionBarDrawerToggle;import android.support.v7.app.AppCompatActivity;import android.support.v7.widget.Toolbar;import android.view.Menu;import android.view.MenuInflater;import android.view.MenuItem;import android.view.View;import android.view.animation.DecelerateInterpolator;import android.widget.FrameLayout;import android.widget.LinearLayout;import android.widget.TextView;import com.example.dounn.menutendina.Database.UtenteLogged;import com.example.dounn.menutendina.Pulsanti.PulsanteNotifica;import com.example.dounn.menutendina.Pulsanti.Search;import com.example.dounn.menutendina.Utility.CallBack;import com.example.dounn.menutendina.Utility.Constants;import com.example.dounn.menutendina.Utility.Images;import com.example.dounn.menutendina.Utility.Request;import com.example.dounn.menutendina.Utility.RequestCallback;import com.example.dounn.menutendina.Utility.Utility;import com.google.gson.Gson;import org.json.JSONException;import org.json.JSONObject;import java.io.File;import java.io.FileNotFoundException;import java.io.InputStream;import java.util.ArrayList;import java.util.List;public class SuperActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {    //dichiaro per gli oggetti di sessione    SharedPreferences pref;    Gson gson;    //gestione della fotocamera    int REQUEST_CODE_GALLERY;    Uri outputFileUri;    DrawerLayout drawer;    ActionBarDrawerToggle toggle;    Search search;    Toolbar toolbar;    Context ctx;    NavigationView navigationView;    FrameLayout superSpinner;    TextView loadingText;    LinearLayout scambioIdee;    private NestedScrollView pageContent;    private PulsanteNotifica pulsanteNotifica;    private TextView errorMessageTextview;    private TextView successMessageTextview;    protected final Object lockUpdateUser = new Object();    protected SwipeRefreshLayout mSwipeRefreshLayout;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        ctx = getApplicationContext();        REQUEST_CODE_GALLERY = 1000;        //inizializzo gli oggetti di sessione        pref = ctx.getSharedPreferences("MyPref", 0); // 0 - for private mode        gson = new Gson();        super.setContentView(R.layout.super_layout);        drawer = (DrawerLayout) findViewById(R.id.layout_drawer);        errorMessageTextview = (TextView) findViewById(R.id.error_message_textview);        successMessageTextview = (TextView) findViewById(R.id.success_message_textview);        pageContent = (NestedScrollView) findViewById(R.id.activity_content);        toolbar = (Toolbar) findViewById(R.id.toolbar);        setSupportActionBar(toolbar);        //CREAZIONE SIA PER L'ITEM DELLA SEARCH CHE PER LA GESTIONE DELL'AUTOCOMPLETE        search = new Search(ctx, getToken());        //TOOGLE (IL SIMBOLO DELLA TENDINA        toggle = new ActionBarDrawerToggle(                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);        drawer.setDrawerListener(toggle);        toggle.syncState();        navigationView = (NavigationView) findViewById(R.id.nav_view);        navigationView.setNavigationItemSelectedListener(this);        superSpinner = (FrameLayout) findViewById(R.id.super_spinner);        loadingText = (TextView) findViewById(R.id.loading_text);        scambioIdee = (LinearLayout) findViewById(R.id.scambio_idee);        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.scroll_down);        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {            @Override            public void onRefresh() {                mSwipeRefreshLayout.setRefreshing(false);                onScrollDownAction();            }        });        // Configure the refreshing colors        mSwipeRefreshLayout.setColorSchemeResources(                R.color.green_cattivo,                android.R.color.holo_green_light,                android.R.color.holo_orange_light,                android.R.color.holo_red_light);        mSwipeRefreshLayout.setEnabled(false);    }    public void startCaricamento(int speed, String text) {        startCaricamentoTimeout(speed, Constants.defaultCaricamentoTimeout, text);    }    void startCaricamentoTimeout(final int speed, final int timeOut, String text) {        synchronized(this) {            loadingText.setText(text);            superSpinner.setAlpha(0f);            superSpinner.setVisibility(View.VISIBLE);            scambioIdee.setAlpha(1f);            scambioIdee.animate().setDuration(speed).alpha(0f).setListener(new AnimatorListenerAdapter() {                @Override                public void onAnimationEnd(Animator animation) {                    scambioIdee.setAlpha(0f);                    scambioIdee.setVisibility(View.INVISIBLE);                }                @Override                public void onAnimationCancel(Animator animation) {                    onAnimationEnd(animation);                }            }).start();            superSpinner.animate().setDuration(speed).alpha(1f).setListener(new AnimatorListenerAdapter() {                @Override                public void onAnimationEnd(Animator animation) {                    superSpinner.setAlpha(1f);                }                @Override                public void onAnimationCancel(Animator animation) {                    onAnimationEnd(animation);                }            }).start();            new Thread(new Runnable() {                @Override                public void run() {                    try {                        Thread.sleep(timeOut);                    } catch(InterruptedException e) {                        e.printStackTrace();                    }                    runOnUiThread(new Runnable() {                        @Override                        public void run() {                            stopCaricamento(speed);                        }                    });                }            }).start();        }    }    public void stopCaricamento(int speed) {        synchronized(this) {            if(scambioIdee.getVisibility() == View.VISIBLE && scambioIdee.getAlpha() == 1f) return;            scambioIdee.setAlpha(0f);            scambioIdee.setVisibility(View.VISIBLE);            superSpinner.setAlpha(1f);            scambioIdee.animate().setDuration(speed).alpha(1f).setListener(new AnimatorListenerAdapter() {                @Override                public void onAnimationEnd(Animator animation) {                    scambioIdee.setAlpha(1f);                }                @Override                public void onAnimationCancel(Animator animation) {                    onAnimationEnd(animation);                }            }).start();            superSpinner.animate().setDuration(speed).alpha(0f).setListener(new AnimatorListenerAdapter() {                @Override                public void onAnimationEnd(Animator animation) {                    superSpinner.setAlpha(0f);                    superSpinner.setVisibility(View.INVISIBLE);                }                @Override                public void onAnimationCancel(Animator animation) {                    onAnimationEnd(animation);                }            }).start();            loadingText.setText("");        }    }    @Override    protected void onResume() {        super.onResume();        Menu menu = navigationView.getMenu();        if(isActivated()) {            menu.setGroupVisible(R.id.anonimous_group, false);            menu.setGroupVisible(R.id.logged_group, true);            if(!getUser().canRecensione()) {                menu.findItem(R.id.nav_inserisci_recensione).setVisible(false);            }        } else {            menu.setGroupVisible(R.id.anonimous_group, true);            menu.setGroupVisible(R.id.logged_group, false);        }        if(pulsanteNotifica != null)            pulsanteNotifica.setNotificationNumber(Utility.notifiche.getNotificationNumber(getToken()));    }    @Override    public void setContentView(int id) {        getLayoutInflater().inflate(id, pageContent);    }    @Override    public void onBackPressed() {        if(drawer.isDrawerOpen(GravityCompat.START)) {            drawer.closeDrawer(GravityCompat.START);        } else {            super.onBackPressed();        }    }    @Override    public boolean onCreateOptionsMenu(Menu menu) {        MenuInflater inflater = getMenuInflater();        Context classe = SuperActivity.this;        if(isActivated()) {            inflater.inflate(R.menu.main, menu);            //INSERIMENTO DELLA CAMPANELLA DELLA NOTIFICA            pulsanteNotifica = new PulsanteNotifica(menu, this);        } else {            inflater.inflate(R.menu.main_senza_notifiche, menu);        }        //aggiungo nel search l'adapter per l'autocompile e il listener        search.search_v(menu, classe, getComponentName());        return true;    }    //funzione per la gestione dei menu , per ora vuota    @Override    public boolean onOptionsItemSelected(MenuItem item) {        //QUANDO CLICCO SUL BOTTONE DEL SEARCH        int id = item.getItemId();        return super.onOptionsItemSelected(item);    }    // funzione per la gestione degli item nella tendina, ad ogni item è associata una Activity    @SuppressWarnings("StatementWithEmptyBody")    @Override    public boolean onNavigationItemSelected(@NonNull MenuItem item) {        Intent intent;        SharedPreferences.Editor editor = pref.edit();        switch(item.getItemId()) {            case R.id.nav_home:                editor.putInt("nav_home", 0);                intent = new Intent(ctx, A10_HomePage.class);                break;            case R.id.nav_registrati:                editor.putInt("nav_registrati", 1);                intent = new Intent(ctx, A1_LoginIniziale.class);                editor.putString("previous", "REG").apply();                break;            case R.id.nav_login:                editor.putInt("nav_login", 2);                intent = new Intent(ctx, A1_LoginIniziale.class);                editor.putString("previous", "LOGIN").apply();                break;            case R.id.nav_notifiche:                editor.putInt("nav_notifiche", 4);                intent = new Intent(ctx, A14_Notifiche.class);                break;            case R.id.nav_mie_recensioni:                editor.putInt("nav_mie_recensioni", 5);                intent = new Intent(ctx, A23_MieRecensioni.class);                break;            case R.id.nav_oggetti_seguo:                editor.putInt("nav_oggetti_seguo", 7);                intent = new Intent(ctx, A22_OggettiSeguiti.class);                break;            case R.id.nav_persone_seguo:                editor.putInt("nav_persone_seguo", 6);                intent = new Intent(ctx, A19_PersoneSeguite.class);                break;            case R.id.nav_inserisci_recensione:                editor.putInt("nav_inserisci_recensione", 8);                intent = new Intent(ctx, A15_InserisciRecensione.class);                break;            case R.id.nav_settings:                editor.putInt("nav_settings", 9);                intent = new Intent(ctx, A6_Impostazioni.class);                break;            case R.id.nav_Assistenza:                editor.putInt("nav_Assistenza", 10);                intent = new Intent(ctx, A35_Assistenza.class);                break;            case R.id.nav_esci:                editor.putInt("nav_esci", 10);                removeUser();                intent = new Intent(ctx, A1_LoginIniziale.class);                break;            case R.id.nav_profilo:                editor.putInt("nav_profilo", 3);                intent = new Intent(ctx, A7_ProfiloPrivato.class);                break;            default:                intent = new Intent(ctx, A10_HomePage.class);                break;        }        editor.commit();        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);        startActivity(intent);        drawer.closeDrawer(GravityCompat.START);        return true;    }    boolean isLogged() {        return getUser() != null;    }    boolean isActivated() {        UtenteLogged user = getUser();        return (user != null) && user.isAttivato();    }    void removeUser() {        setUser(null);    }    public String getToken() {        if(isLogged()) return getUser().getToken();        else return null;    }    public synchronized UtenteLogged getUser() {        String json = pref.getString("user", "");        if(json.equals("")) return null;        return gson.fromJson(json, UtenteLogged.class);    }    public synchronized void setUser(UtenteLogged utente) {        String json = gson.toJson(utente);        SharedPreferences.Editor editor = pref.edit();        editor.putString("user", json);        editor.apply();    }    public synchronized void attivaUser() {        UtenteLogged user = getUser();        if(user == null) return;        user.setAttivato(true);        setUser(user);    }    void setGone(View... v) {        for(View a : v) a.setVisibility(View.GONE);    }    void setVisible(View... v) {        for(View a : v) a.setVisibility(View.VISIBLE);    }    public void noInternetErrorBar() {        errorBar(getResources().getString(R.string.no_internet), 5000);    }    public void errorBar(final String text, final int millis) {        runOnUiThread(new Runnable() {            @Override            public void run() {                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);                errorMessageTextview.setLayoutParams(params);                errorMessageTextview.setText(text);                errorMessageTextview.setVisibility(View.VISIBLE);                new Thread() {                    @Override                    public void run() {                        try {                            Thread.sleep(millis);                        } catch(InterruptedException ignored) {                        }                        runOnUiThread(new Runnable() {                            @Override                            public void run() {                                final int prevHeight = errorMessageTextview.getHeight();                                errorMessageTextview.setVisibility(View.VISIBLE);                                ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, 0);                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {                                    @Override                                    public void onAnimationUpdate(ValueAnimator animation) {                                        errorMessageTextview.getLayoutParams().height = (int) animation.getAnimatedValue();                                        errorMessageTextview.requestLayout();                                    }                                });                                valueAnimator.addListener(new AnimatorListenerAdapter() {                                    @Override                                    public void onAnimationEnd(Animator animation) {                                        errorMessageTextview.setVisibility(View.GONE);                                        errorMessageTextview.getLayoutParams().height = prevHeight;                                    }                                });                                valueAnimator.setInterpolator(new DecelerateInterpolator());                                valueAnimator.setDuration(500);                                valueAnimator.start();                            }                        });                    }                }.start();            }        });    }    public void successBar(final String text, final int millis) {        runOnUiThread(new Runnable() {            @Override            public void run() {                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);                successMessageTextview.setLayoutParams(params);                successMessageTextview.setText(text);                successMessageTextview.setVisibility(View.VISIBLE);                new Thread() {                    @Override                    public void run() {                        try {                            Thread.sleep(millis);                        } catch(InterruptedException ignored) {                        }                        runOnUiThread(new Runnable() {                            @Override                            public void run() {                                final int prevHeight = successMessageTextview.getHeight();                                successMessageTextview.setVisibility(View.VISIBLE);                                ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, 0);                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {                                    @Override                                    public void onAnimationUpdate(ValueAnimator animation) {                                        successMessageTextview.getLayoutParams().height = (int) animation.getAnimatedValue();                                        successMessageTextview.requestLayout();                                    }                                });                                valueAnimator.addListener(new AnimatorListenerAdapter() {                                    @Override                                    public void onAnimationEnd(Animator animation) {                                        successMessageTextview.setVisibility(View.GONE);                                        successMessageTextview.getLayoutParams().height = prevHeight;                                    }                                });                                valueAnimator.setInterpolator(new DecelerateInterpolator());                                valueAnimator.setDuration(500);                                valueAnimator.start();                            }                        });                    }                }.start();            }        });    }    void updateUser(final CallBack b) {        synchronized(lockUpdateUser) {            try {                JSONObject req = new JSONObject();                req.put("token", getToken());                req.put("path", "update");                new Request(new RequestCallback() {                    @Override                    public void inTheEnd(JSONObject a) {                        try {                            if(a.getString("status").equals("OK")) {                                // INIZIALIZZAZIONE DATI SCARICATI                                setUser(new UtenteLogged(a.getJSONObject("result").getJSONObject("utente")));                                if(b != null) b.inTheEnd(a);                            } else {                                noInternetErrorBar();                            }                        } catch(Exception e) {                            e.printStackTrace();                        }                    }                    @Override                    public void noInternetConnection() {                        noInternetErrorBar();                    }                }).execute(req);            } catch(JSONException e) {                e.printStackTrace();            }        }    }    //controllo uguaglianza password    public boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {        return confirmPassword != null && !confirmPassword.equals("") && password.length() > 7 && (password.equals(confirmPassword));    }    //controllo validità email    public boolean isValidEmail(CharSequence target) {        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();    }    public boolean checkUsername(CharSequence username) {        return username != null && username.toString().length() > 0;    }    @Override    protected void onDestroy() {        super.onDestroy();        stopCaricamento(0);    }    //FUNZIONE PER APRIRE FOTOCAMERA E GALLERIA    void openImageIntent() {        // Determine Uri of camera image to save.        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);        root.mkdirs();        final String fname = "img_" + System.currentTimeMillis() + ".jpg";        final File sdImageMainDirectory = new File(root, fname);        outputFileUri = Uri.fromFile(sdImageMainDirectory);        // Camera.        final List<Intent> cameraIntents = new ArrayList<>();        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);        final PackageManager packageManager = getPackageManager();        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);        for(ResolveInfo res : listCam) {            final String packageName = res.activityInfo.packageName;            final Intent intent = new Intent(captureIntent);            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));            intent.setPackage(packageName);            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);            cameraIntents.add(intent);        }        // Filesystem.        final Intent galleryIntent = new Intent();        galleryIntent.setType("image/*");        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);        // Chooser of filesystem options.        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");        // Add the camera options.        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));        startActivityForResult(chooserIntent, REQUEST_CODE_GALLERY);    }    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        if(resultCode == RESULT_OK) {            if(requestCode == REQUEST_CODE_GALLERY) {                final boolean isCamera;                if(data == null) {                    isCamera = true;                } else {                    final String action = data.getAction();                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);                }                Uri selectedImageUri;                if(isCamera) {                    selectedImageUri = outputFileUri;                } else {                    selectedImageUri = data.getData();                }                try {                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);                    onActivityResultImage(Images.resizeDefaultBitmap(bitmap));                } catch(FileNotFoundException e) {                    e.printStackTrace();                }            }        }    }    void onActivityResultImage(Bitmap b) {        //Toast.makeText(ctx, "Gestire onActivityResultImage(Bitmap b)", Toast.LENGTH_LONG).show();    }    void enableOnScrollDownAction() {        mSwipeRefreshLayout.setEnabled(true);    }    @CallSuper    public void onScrollDownAction() {        mSwipeRefreshLayout.setRefreshing(false);    }    public void setMenuChecked(String prefName) {        navigationView.getMenu().getItem(pref.getInt(prefName, 0)).setChecked(true);    }}