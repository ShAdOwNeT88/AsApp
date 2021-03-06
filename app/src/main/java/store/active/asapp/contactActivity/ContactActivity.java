package store.active.asapp.contactActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.dd.CircularProgressButton;

import store.active.asapp.R;
import store.active.asapp.homeActivity.MainActivity;
import store.active.asapp.locationActivity.LocationActivity;
import store.active.asapp.models.Mail;
import store.active.asapp.ticketActivity.TicketActivity;
import utility.UrlRedirect;

import static java.lang.Thread.sleep;

public class ContactActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    boolean filledName,filledResponse,filledObject, filledMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get the emailAddress from string.xml file
        final String ADDRESSOFEMAILTOSEND = getString(R.string.AddressOfMailToSend);
        //get the context
        final Context context = this.getApplicationContext();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_contact_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final EditText etName = (EditText) findViewById(R.id.editTextName);
        final EditText etMailResponse = (EditText) findViewById(R.id.editTextMail);
        final EditText etObject = (EditText) findViewById(R.id.editTextSubject);
        final EditText etMessage = (EditText) findViewById(R.id.editTextMessage);
        final CharSequence text = "Email inviata riceverai una risposta il prima possibile!";

        final CircularProgressButton btnSend = (CircularProgressButton) findViewById(R.id.buttonSubmit);
        //Set button state to initial condition.
        btnSend.setProgress(0);
        btnSend.setEnabled(false);

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFields(etName,etMailResponse,etObject,etMessage,btnSend);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //extract Text from the EditText
                String name = etName.getText().toString();
                String mailaddress = etMailResponse.getText().toString();
                String object = etObject.getText().toString();
                String message = etMessage.getText().toString();

                //Merge message body with mail address saved in field "Email di contatto."
                String message_with_mailaddress = message.concat("\n").concat("Preghiamo di rispondere al seguente indirizzo email: ")
                        .concat("\n").concat(mailaddress).concat("\n").concat("Cordiali Saluti.").concat("\n");

                //Make mail object
                Mail mail = new Mail(name,mailaddress,object,message_with_mailaddress);
                //Set-up presenter
                ContactPresenter cp = new ContactPresenter(mail,context,ADDRESSOFEMAILTOSEND);
                //launch activity
                startActivity(Intent.createChooser(cp.sendMail(), "Invio email..."));

                //Bundle for extras of the intent.
                Bundle bundle = cp.sendMail().getExtras();

                //Get Extra Value for sync email send with the UI
                if(bundle.getBoolean("IS_SENDED")){
                    Log.e("Mail Inviata","Mail Inviata");
                    //launch snackbar to view the error message
                    Snackbar.make(v, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    //Change button state to sended.
                    btnSend.setProgress(100);
                    //clear the fields filled by the user
                    cp.clearFields(etName,etMailResponse,etObject,etMessage,btnSend);
                }

            }
        });
    }

    //Check if all EditText is filled with some text and activate Button.
    public void validateFields(EditText etName, EditText etMailResponse, EditText etObject, EditText etMessage, CircularProgressButton btnSend){
        if ((etName.getText().length() > 0) && (etMailResponse.getText().length() > 0) && (etObject.getText().length() > 0) && (etMessage.getText().length() > 0)){
            btnSend.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_contact_activity);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.send_email:
                Intent mailIntent = new Intent(this, ContactActivity.class);
                startActivity(mailIntent);
                break;
            case R.id.send_failure_ticket:
                Intent ticketIntent = new Intent(this, TicketActivity.class);
                startActivity(ticketIntent);
                break;
            case R.id.position:
                Intent positionIntent = new Intent(this, LocationActivity.class);
                startActivity(positionIntent);
                break;
            case R.id.author:
                UrlRedirect urlRed = new UrlRedirect(this.getApplicationContext(),getString(R.string.linkedinDeveloper));
                urlRed.redirect();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_contact_activity);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
