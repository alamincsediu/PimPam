package jcollado.pw.pimpam.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jcollado.pw.pimpam.R;
import jcollado.pw.pimpam.model.Comic;
import jcollado.pw.pimpam.model.Database;
import jcollado.pw.pimpam.model.FactoryComic;
import jcollado.pw.pimpam.model.FactorySerie;
import jcollado.pw.pimpam.model.Serie;
import jcollado.pw.pimpam.utils.BaseFragment;
import jcollado.pw.pimpam.utils.FirebaseModule;
import jcollado.pw.pimpam.utils.Functions;
import jcollado.pw.pimpam.widgets.SquareImageView;


public class AddComicFragment extends BaseFragment {

    public static final int PLACE_IN_DRAWER = 2;
    private static final int GALLERY_PICK = 1;
    private static final int CAMERA_PICK = 2;
    private boolean imageChanged = false;
    private Bitmap imageBitmap;

    @BindView(R.id.nameED)
    EditText nameED;
    @BindView(R.id.anyoED)
    EditText anyoED;
    @BindView(R.id.editorialED)
    EditText editorialED;
    @BindView(R.id.numeroED)
    EditText numeroED;

    @BindView(R.id.comicIV)
    SquareImageView comicIV;
    @BindView(R.id.toolbar2)
    Toolbar toolbar;
    @BindView(R.id.serieAC)
    AutoCompleteTextView serieAC;

    FirebaseStorage storage;
    String imageURL;
    static Comic comic;
    static Serie serie;

    private OnFragmentInteractionListener mListener;
    String[] languages={"Android ","java","IOS","SQL","JDBC","Web services"};

    public AddComicFragment() {
        // Required empty public constructor
    }


    public static AddComicFragment newInstance() {
        AddComicFragment fragment = new AddComicFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_comic, container, false);
        ButterKnife.bind(this, view);

        prepareSpinner();
        prepareToolbar();

        storage = FirebaseStorage.getInstance();
        return view ;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            imageChanged = true;
            if (requestCode == CAMERA_PICK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                comicIV.setImageBitmap(imageBitmap);
            } else {
                comicIV.setImageURI(data.getData());

            }
        }
    }

    private void onItemSerieACTVListener(){
        serieAC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                Serie serie = (Database.getInstance().getSerieByName(serieAC.getText().toString()));
               anyoED.setText(serie.getYear());
                editorialED.setText(serie.getEditorial());
            }
        });
    }

    @OnClick(R.id.addFab) void submit() {
        hideKeyboard();
        if (serieAC.getText().toString().equals("")) {
            serieAC.setError(getString(R.string.empty_name_comic));

        }
        else if (editorialED.getText().toString().equals("")) {
            editorialED.setError(getString(R.string.empty_editorial_comic));


        }
        else if (numeroED.getText().length() == 0) {
            numeroED.setError(getString(R.string.empty_numero_comic));
        }
        else {

            comicIV.setDrawingCacheEnabled(true);
            comicIV.buildDrawingCache();
            Bitmap bitmap = comicIV.getDrawingCache();
            imageURL = FirebaseModule.getInstance().uploadBitmap(bitmap,java.util.UUID.randomUUID().toString(),this);



            serie = Database.getInstance().getSerieByName(serieAC.getText().toString());
//            nameED.setText(Integer.toString(Singleton.getInstance().getDatabase().getSeries().get(0).getVolumenesName().size()));
            if(serie == null) {
                serie = FactorySerie.createSerie(serieAC.getText().toString(),anyoED.getText().toString() , editorialED.getText().toString());
            }

            comic = FactoryComic.createComic(nameED.getText().toString(),editorialED.getText().toString(),
                    "",numeroED.getText().toString(),anyoED.getText().toString(),serie,false);

        }
    }
    public void uploadComic(String imageURL){
        Database.getInstance().serieToDatabase(serie);
        comic.setImageURL(imageURL);

        serie.addComicToSerie(comic);

        Database.getInstance().comicToDatabase(comic,this);


    }
    @Override
    public void comicUploaded(){
        onConnectionFinished();
        new Toast(getContext()).makeText(getContext(),getString(R.string.comic_addded_succesfull), Toast.LENGTH_SHORT).show();
        CollectionFragment collection = CollectionFragment.newInstance();
         openFragment(collection,collection.PLACE_IN_DRAWER);
    }


    @OnClick(R.id.comicIV)
    public void changeImage() {

        AlertDialog.Builder builder = Functions.getModal(R.string.warning_modal_title,R.string.news_image_message, getActivity());
        builder.setPositiveButton(R.string.news_image_camera, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PICK);
            }
        });
        builder.setNeutralButton(R.string.news_image_gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, GALLERY_PICK);
            }
        });
        builder.show();

    }
    private void stopRefreshing() {

        onConnectionFinished();
    }
    private void prepareSpinner(){

        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,Database.getInstance().getSeriesNameArray());

        serieAC.setAdapter(adapter);
        serieAC.setThreshold(1);
        onItemSerieACTVListener();

    }
    private void prepareToolbar(){
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.hamburger));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openDrawer();
            }
        });

        toolbar.setTitle(getString(R.string.addComic));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void openFragment(BaseFragment fragment,int drawerSelection){

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment,"")
                .commit();
        MainActivity.result.setSelection(drawerSelection);
    }
}
