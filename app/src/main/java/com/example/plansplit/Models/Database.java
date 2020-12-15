package com.example.plansplit.Models;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.plansplit.Controllers.FragmentControllers.personal.PersonalFragment;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Models.Objects.Expense;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.FriendRequest;
import com.example.plansplit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static androidx.constraintlayout.widget.Constraints.TAG;

import kotlin.Suppress;

public class Database {

    //error list for easy debugging
    private static final String DATABASE_ERROR = "DATABASE_ERROR";
    private static final String KEY_NOT_FOUND = "KEY_NOT_FOUND";
    private static final String FRIEND_LIST_EMPTY = "FRIEND_LIST_EMPTY";
    private static final String VALUE_NOT_FOUND = "VALUE_NOT_FOUND";
    private static final String USER_AND_TARGET_KEY_SAME = "USER_AND_TARGET_KEY_SAME";
    private static final String ALREADY_FRIENDS = "ALREADY_FRIENDS";
    private static final String ALREADY_SENT_FRIEND_REQUEST = "ALREADY_SENT_FRIEND_REQUEST";
    private static final String TAG = "DATABASE";
    final int[] butce = new int[1];
    public boolean ctrlRun=false;
    private Context context;
    private Fragment fragment;
    private int totExpense = 0;

    public int getTotExpense() {
        return totExpense;
    }

    public int getButce() {
        return butce[0];
    }

    private String userId = null;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    //Firebase
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference user_reference = database.getReference("users");

    public Database(){ }

    public Database(Context context){
        this.context=context;
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        if (acct != null) {
            setUserId(acct.getId());
            System.out.println("acct not null");
        }else{
            System.out.println("acct null");
        }
    }

    public Database(Context context, Fragment fragment){
        this.fragment = fragment;
        this.context=context;
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        if (acct != null) {
            setUserId(acct.getId());
            System.out.println("acct not null");
        }else{
            System.out.println("acct null");
        }
    }

    private static class Holder{
        private static final Database INSTANCE = new Database();
    }

    //Singleton
    public static Database getInstance(){
        return Holder.INSTANCE;
    }

    /**
     * Interface for receiving a Friend object.
     * One method with successfully retrieving the object, other one for errors
     */
    public interface FriendCallBack {
        void onFriendRetrieveSuccess(Friend friend);
        void onError(String error_tag, String error);
    }

    /**
     * Interface for receiving a FriendRequest object.
     * One method with successfully retrieving the object, other one for errors
     */
    public interface FriendRequestCallBack {
        void onFriendRequestRetrieveSuccess(FriendRequest friend_request);
        void onError(String error_tag, String error);
    }

    /**
     * Standard interface for status report from Database operations
     */
    public interface DatabaseCallBack {
        void onSuccess(String success);
        void onError(String error_tag, String error);
    }

    /**
     * A private handler for adding new friend and removing request
     */
    @SuppressWarnings("unused")
    private interface AddFriendHandler {
        void addAsFriend(String key, ArrayList<String> friends);
        void removeFromRequests(String key, ArrayList<String> friend_reqs);
    }

    /**
     * A private handler for adding new friend request
     */
    @SuppressWarnings("unused")
    private interface SendFriendRequestHandler {
        void addToRequests(String key, ArrayList<String> friend_reqs);
    }

    /**
     * A private handler for declining the friend request
     */
    @SuppressWarnings("unused")
    private interface DeclineFriendRequestHandler {
        void declineFriendRequest(String key, ArrayList<String> friend_reqs);
    }

    /**
     * A private handler for removing friend from friend list
     */
    @SuppressWarnings("unused")
    private interface RemoveFriendHandler {
        void removeFriend(String key, ArrayList<String> friends);
    }

    /**
     * Creates a child with "key" under user reference. No controls are made, so before use it
     * parameters should be checked
     * @param key Google ID
     * @param name display name of google account
     * @param email current email of google account
     * @param surname surname of google account
     */
    public void registerUser(String key, String name, String email, String surname){
        user_reference.child(key).child("name").setValue(name);
        user_reference.child(key).child("email").setValue(email);
        user_reference.child(key).child("surname").setValue(surname);
    }

    /**
     * It searchs for all friends of the given keys' user
     * then calls getFriend method to construct the Friend and calls CallBack,
     * which should be specialized
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value FRIEND_LIST_EMPTY} <p>
     * @param user_key unique key whose friends would be searched for
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see #getFriend(String, FriendCallBack)
     */
    public void getFriends(final String user_key, final FriendCallBack callBack){
        if (user_key == null){
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND,
                            user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if(friends == null){
                    callBack.onError(FRIEND_LIST_EMPTY, "Arkadaş listesi boş");
                    return;
                }
                for(final String friend_key: friends){
                    getFriend(friend_key, callBack);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It constructs the Friend object of the given key and send it via FriendCallBack
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * @param friend_key key which would be searched and then constructed as a Friend
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see Friend
     */
    public void getFriend(final String friend_key, final FriendCallBack callBack){
        if (friend_key == null){
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        user_reference.child(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    callBack.onError(KEY_NOT_FOUND, friend_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                /*fixme: kayıt sırasında resim almadığımızdan
                            rastgele resim koydum.
                            borçların database'de tutulma yöntemi geçici,
                            acilen değişmeli
                */
                int photo = R.drawable.denemeresim;
                String name = snapshot.child("name").getValue().toString();
                if(name.equals("No name")){
                    //if there was one name when user login "No name" would be used, in such case
                    //using email as name would be better to show user to recognize the person
                    name = snapshot.child("email").getValue().toString();
                }
                int amount = 0;
                Friend friend = new Friend(photo, name, amount, friend_key);
                callBack.onFriendRetrieveSuccess(friend);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It checks given keys friends list and searches for specific keys presence
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value USER_AND_TARGET_KEY_SAME} <p>
     * {@value VALUE_NOT_FOUND} <p>
     * @param user_key key whose friends list would be searched
     * @param friend_key searched key
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see Friend
     */
    public void isFriendExists(final String user_key, final String friend_key, final DatabaseCallBack callBack){
        if (user_key == null){
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if (friend_key == null){
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        if (user_key.equals(friend_key)){
            callBack.onError(USER_AND_TARGET_KEY_SAME, "Arkadaş keyi ile kullanıcı keyi aynı");
            return;
        }
        //assuming there is a user with "user_key", if there is not, an Exception will be thrown
        user_reference.child(user_key).child("friends").orderByValue().equalTo(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callBack.onError(VALUE_NOT_FOUND, "böyle bir arkadaş yok");
                    return;
                }
                String email = (String) snapshot.child(friend_key).child("email").getValue();
                callBack.onSuccess(email + " arkadaş listesinde bulunuyor");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It searchs for all friend requests of the given keys' user
     * then calls getFriendRequest method to construct the FriendRequest and calls CallBack,
     * which should be specialized
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value FRIEND_LIST_EMPTY} <p>
     * @param user_key unique key whose friend requests would be searched for
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see #getFriendRequest(String, FriendRequestCallBack) 
     */
    public void getFriendRequests(final String user_key, final FriendRequestCallBack callBack){
        if (user_key == null){
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND,
                            user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if(friend_reqs == null){
                    callBack.onError(FRIEND_LIST_EMPTY, "Arkadaş istekleri listesi boş");
                    return;
                }
                for(final String request_key: friend_reqs){
                    getFriendRequest(request_key, callBack);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It constructs the FriendRequest object of the given key and send it via FriendRequestCallBack
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * @param request_key key which would be searched and then constructed as a Friend
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see FriendRequest
     */
    public void getFriendRequest(final String request_key, final FriendRequestCallBack callBack){
        if (request_key == null){
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        user_reference.child(request_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    callBack.onError(KEY_NOT_FOUND, request_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                /*fixme: kayıt sırasında resim almadığımızdan
                            rastgele resim koydum.
                */
                int photo = R.drawable.denemeresim;
                String name = snapshot.child("name").getValue().toString();
                if(name == null){
                    //if somehow null is present w/o Exception, use email as name, as it can not be null
                    name = snapshot.child("email").getValue().toString();
                }
                String email = snapshot.child("email").getValue().toString();
                FriendRequest friend_request = new FriendRequest(photo, name, email, request_key);
                callBack.onFriendRequestRetrieveSuccess(friend_request);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It searches for given email, if any user is registered with this email,
     * if so, looks if the user and emails owner already friends, already sent request
     * or they are same user. If no error shows up, calls searchInFriendRequest, which searches
     * the users own friend requests list to determine if already a request from the other user sent.
     * If so, rather than sending request, they will be directly added as Friend via addAsFriend method.
     * If not sendFriendRequestInner method will be called to send the requests
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value VALUE_NOT_FOUND} <p>
     * {@value USER_AND_TARGET_KEY_SAME} <p>
     * {@value ALREADY_FRIENDS} <p>
     * {@value ALREADY_SENT_FRIEND_REQUEST} <p>
     * @param user_key key of this user
     * @param email email of the searched account, whom friend request will be send
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see #searchInFriendRequests(String, String, DatabaseCallBack)
     */
    public void sendFriendRequest(final String user_key, final String email, final DatabaseCallBack callBack){
        if (user_key == null){
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        user_reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    callBack.onError(VALUE_NOT_FOUND, email + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                //make snapshot usable, which points directly the user with "email"
                snapshot = snapshot.getChildren().iterator().next();
                if (user_key.equals(snapshot.getKey())){
                    callBack.onError(USER_AND_TARGET_KEY_SAME, email + " kendine istek yollamaya çalışıyor");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if (friends != null){
                    for (String key: friends){
                        if (user_key.equals(key)){
                            callBack.onError(ALREADY_FRIENDS, "zaten arkadaşlar");
                            return;
                        }
                    }
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if (friend_reqs != null){
                    for(String key: friend_reqs){
                        if (user_key.equals(key)){
                            callBack.onError(ALREADY_SENT_FRIEND_REQUEST, "bu emaile daha önce istek yollanmış");
                            return;
                        }
                    }
                }
                searchInFriendRequests(user_key, snapshot.getKey(), callBack);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It sends friend request to target account, it is a private method as it does not make controls
     * and should not be used directly, but should be called after control method
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * @param user_key key of this user
     * @param friend_key key of the other user, whom friend reqeust will be send
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     */
    private void sendFriendRequestInner(@NonNull final String user_key, @NonNull final String friend_key, final DatabaseCallBack callBack){
        final SendFriendRequestHandler handler = new SendFriendRequestHandler() {
            @Override
            public void addToRequests(String key, ArrayList<String> friend_reqs) {
                user_reference.child(key).child("friend_reqs").setValue(friend_reqs);
                callBack.onSuccess("arkadaşlık isteği gönderildi");
            }
        };
        user_reference.child(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    callBack.onError(KEY_NOT_FOUND, friend_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if(friend_reqs == null){
                    friend_reqs = new ArrayList<>();
                }
                friend_reqs.add(user_key);
                handler.addToRequests(friend_key, friend_reqs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * It searches given key in users friend requests, if it shows up calls addASFriend,
     * if not calls SendFriendRequestInner method.
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * @param user_key key of this user, whose friend requests list will be searched
     * @param searched_key key of the other user, which is searched in this users requests list
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     * @see #sendFriendRequestInner(String, String, DatabaseCallBack)
     * @see #addAsFriend(String, String, DatabaseCallBack)
     */
    private void searchInFriendRequests(final String user_key, final String searched_key, final DatabaseCallBack callBack){
        if(user_key == null){
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if(searched_key == null){
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callBack.onError(KEY_NOT_FOUND, user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if(friend_reqs != null){
                    for (String key: friend_reqs) {
                        if(searched_key.equals(key)){
                            addAsFriend(user_key, searched_key, callBack);
                            return;
                        }
                    }
                }
                sendFriendRequestInner(user_key, searched_key, callBack);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Adds both keys to one another's friends list
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value USER_AND_TARGET_KEY_SAME} <p>
     * {@value ALREADY_FRIENDS} <p>
     * @param user_key key of this user
     * @param friend_key key of friend request
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     */
    public void addAsFriend(final String user_key, final String friend_key, final DatabaseCallBack callBack){
        if(user_key == null){
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if(friend_key == null){
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        boolean same_key = false;
        if (user_key.equals(friend_key)){
            same_key = true;
        }
        final AddFriendHandler handler = new AddFriendHandler() {
            @Override
            public void addAsFriend(String key, ArrayList<String> friends) {
                user_reference.child(key).child("friends").setValue(friends);
            }

            @Override
            public void removeFromRequests(String key, ArrayList<String> friend_reqs) {
                user_reference.child(key).child("friend_reqs").setValue(friend_reqs);
            }
        };
        final boolean finalSame_key = same_key;
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    callBack.onError(KEY_NOT_FOUND, user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if(friends == null){
                    friends = new ArrayList<>();
                }
                friends.add(friend_key);
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if(friend_reqs == null){
                    friend_reqs = new ArrayList<>();
                }
                friend_reqs.remove(friend_key);
                if (finalSame_key){
                    handler.removeFromRequests(user_key, friend_reqs);
                    callBack.onError(USER_AND_TARGET_KEY_SAME, "Arkadaş eklenecek key ile kullanıcı keyi aynı");
                    return;
                }
                if (friends.contains(friend_key)){
                    handler.removeFromRequests(user_key, friend_reqs);
                    callBack.onError(ALREADY_FRIENDS, "zaten arkadaşlar");
                    return;
                }
                handler.addAsFriend(user_key, friends);
                handler.removeFromRequests(user_key, friend_reqs);
                callBack.onSuccess("arkadaş eklendi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
        user_reference.child(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    callBack.onError(KEY_NOT_FOUND, friend_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if(friends == null){
                    friends = new ArrayList<>();
                }
                friends.add(user_key);
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if(friend_reqs == null){
                    friend_reqs = new ArrayList<>();
                }
                friend_reqs.remove(user_key);
                if (finalSame_key){
                    handler.removeFromRequests(friend_key, friend_reqs);
                    //no error will be send as it would be send in user_key's snapshot
                    return;
                }
                if (friends.contains(user_key)){
                    handler.removeFromRequests(friend_key, friend_reqs);
                    //no error will be send as it would be send in user_key's snapshot
                    return;
                }
                handler.addAsFriend(friend_key, friends);
                handler.removeFromRequests(friend_key, friend_reqs);
                //no success will be send as it would be send in user_key's snapshot
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Removes friend request from this users friend requests list
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value USER_AND_TARGET_KEY_SAME} <p>
     * {@value VALUE_NOT_FOUND} <p>
     * @param user_key key of this user
     * @param request_key key of friend request
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     */
    public void declineFriendRequest(final String user_key, final String request_key, final DatabaseCallBack callBack){
        if (user_key == null){
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if (request_key == null){
            callBack.onError(KEY_NOT_FOUND, "aranan key null");
            return;
        }
        boolean same_key = false;
        if (user_key.equals(request_key)){
            same_key = true;
        }
        final DeclineFriendRequestHandler handler = new DeclineFriendRequestHandler() {
            @Override
            public void declineFriendRequest(String key, ArrayList<String> friend_reqs) {
                user_reference.child(key).child("friend_reqs").setValue(friend_reqs);
            }
        };
        final boolean finalSame_key = same_key;
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callBack.onError(KEY_NOT_FOUND, user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friend_reqs = (ArrayList<String>)
                        snapshot.child("friend_reqs").getValue();
                if (friend_reqs == null){
                    friend_reqs = new ArrayList<>();
                }
                boolean is_request_exists = friend_reqs.remove(request_key);
                if (!is_request_exists){
                    callBack.onError(VALUE_NOT_FOUND, "Böyle bir istek bulunamadı");
                    return;
                }
                handler.declineFriendRequest(user_key, friend_reqs);
                if (finalSame_key){
                    callBack.onError(USER_AND_TARGET_KEY_SAME, "Silinecek isteğin keyi ile kullanıcı keyi aynı");
                    //even though decline method successfully called, still an error will be sent,
                    //as it should not be happening in the first place
                }
                callBack.onSuccess("İstek silindi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    /**
     * Removes each others keys from their friends list
     * <p>
     * ERRORS: <p>
     * {@value DATABASE_ERROR} <p>
     * {@value KEY_NOT_FOUND} <p>
     * {@value VALUE_NOT_FOUND} <p>
     * @param user_key key of this user
     * @param friend_key key of friend
     * @param callBack the callBack to be called whenever an error occurs or task successfully end
     */
    public void removeFriend(final String user_key, final String friend_key, final DatabaseCallBack callBack){
        if (user_key == null){
            callBack.onError(KEY_NOT_FOUND, "kullanıcı keyi null");
            return;
        }
        if (friend_key == null){
            callBack.onError(KEY_NOT_FOUND, "silinecek key null");
            return;
        }
        final RemoveFriendHandler handler = new RemoveFriendHandler() {
            @Override
            public void removeFriend(String key, ArrayList<String> friends) {
                user_reference.child(key).child("friends").setValue(friends);
            }
        };
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callBack.onError(KEY_NOT_FOUND, user_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if (friends == null) {
                    friends = new ArrayList<>();
                }
                boolean is_friend_exists = friends.remove(friend_key);
                if (!is_friend_exists) {
                    callBack.onError(VALUE_NOT_FOUND, "Böyle bir arkadaş bulunamadı");
                    return;
                }
                handler.removeFriend(user_key, friends);
                callBack.onSuccess("İstek silindi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
        user_reference.child(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    callBack.onError(KEY_NOT_FOUND, friend_key + " ile ilişkili kullanıcı bulunamadı");
                    return;
                }
                @SuppressWarnings("unchecked")
                ArrayList<String> friends = (ArrayList<String>)
                        snapshot.child("friends").getValue();
                if (friends == null){
                    friends = new ArrayList<>();
                }
                boolean is_friend_exists = friends.remove(user_key);
                if (!is_friend_exists){
                    callBack.onError(VALUE_NOT_FOUND, "Böyle bir arkadaş bulunamadı");
                    return;
                }
                handler.removeFriend(friend_key, friends);
                //no success will be send as it would be send in user_key's snapshot
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(DATABASE_ERROR, error.getMessage());
            }
        });
    }

    public void getBudget(){
        user_reference.child(userId).child("budget").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String b;
                    b = snapshot.getValue().toString();
                    ((PersonalFragment) fragment).checkBudget(b);
                }else {
                    ((PersonalFragment) fragment).checkBudget(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setBudget(int budget){
        user_reference.child(userId).child("budget").setValue(budget);
    }

    public void addExpense(String name, String type, String price){

        DatabaseReference dbRef = user_reference.child(userId).child("expenses");
        String key = dbRef.push().getKey();
        DatabaseReference dbr = dbRef.child(key);
        dbr.child("name").setValue(name);
        dbr.child("type").setValue(type);
        dbr.child("price").setValue(price);
        getExpenses();

    }

    public ArrayList getExpenses() {
        final ArrayList<Expense> expenses = new ArrayList<>();
        DatabaseReference dbRef = user_reference.child(userId).child("expenses");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totExpense=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = (String) ds.child("name").getValue();
                    String type = (String) ds.child("type").getValue();
                    String price = (String) ds.child("price").getValue();
                    if(price!=null) {
                        int p = Integer.parseInt(price);
                        totExpense+=p;
                        expenses.add(new Expense(name, type, p));
                    }

                }
                ((PersonalFragment)fragment).newExpense(expenses, totExpense);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return expenses;
    }
}
