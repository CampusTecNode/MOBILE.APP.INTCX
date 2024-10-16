// Generated by view binder compiler. Do not edit!
package com.intec.connect.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.button.MaterialButton;
import com.intec.connect.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityLoginBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout constraintLayout;

  @NonNull
  public final ConstraintLayout constraintLayout2;

  @NonNull
  public final TextView endLine;

  @NonNull
  public final TextView forgotPasswordTextView;

  @NonNull
  public final ImageView logo;

  @NonNull
  public final ConstraintLayout mainContainer;

  @NonNull
  public final LinearLayout orContainer;

  @NonNull
  public final TextView orTextView;

  @NonNull
  public final MaterialButton outlookButton;

  @NonNull
  public final TextView password;

  @NonNull
  public final EditText passwordEditText;

  @NonNull
  public final MaterialButton signInButton;

  @NonNull
  public final TextView startLine;

  @NonNull
  public final TextView titleTextView;

  @NonNull
  public final TextView vatId;

  @NonNull
  public final EditText vatIdEditText;

  @NonNull
  public final TextView welcomeTextView;

  private ActivityLoginBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout constraintLayout, @NonNull ConstraintLayout constraintLayout2,
      @NonNull TextView endLine, @NonNull TextView forgotPasswordTextView, @NonNull ImageView logo,
      @NonNull ConstraintLayout mainContainer, @NonNull LinearLayout orContainer,
      @NonNull TextView orTextView, @NonNull MaterialButton outlookButton,
      @NonNull TextView password, @NonNull EditText passwordEditText,
      @NonNull MaterialButton signInButton, @NonNull TextView startLine,
      @NonNull TextView titleTextView, @NonNull TextView vatId, @NonNull EditText vatIdEditText,
      @NonNull TextView welcomeTextView) {
    this.rootView = rootView;
    this.constraintLayout = constraintLayout;
    this.constraintLayout2 = constraintLayout2;
    this.endLine = endLine;
    this.forgotPasswordTextView = forgotPasswordTextView;
    this.logo = logo;
    this.mainContainer = mainContainer;
    this.orContainer = orContainer;
    this.orTextView = orTextView;
    this.outlookButton = outlookButton;
    this.password = password;
    this.passwordEditText = passwordEditText;
    this.signInButton = signInButton;
    this.startLine = startLine;
    this.titleTextView = titleTextView;
    this.vatId = vatId;
    this.vatIdEditText = vatIdEditText;
    this.welcomeTextView = welcomeTextView;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.constraint_layout;
      ConstraintLayout constraintLayout = ViewBindings.findChildViewById(rootView, id);
      if (constraintLayout == null) {
        break missingId;
      }

      id = R.id.constraintLayout2;
      ConstraintLayout constraintLayout2 = ViewBindings.findChildViewById(rootView, id);
      if (constraintLayout2 == null) {
        break missingId;
      }

      id = R.id.end_line;
      TextView endLine = ViewBindings.findChildViewById(rootView, id);
      if (endLine == null) {
        break missingId;
      }

      id = R.id.forgotPasswordTextView;
      TextView forgotPasswordTextView = ViewBindings.findChildViewById(rootView, id);
      if (forgotPasswordTextView == null) {
        break missingId;
      }

      id = R.id.logo;
      ImageView logo = ViewBindings.findChildViewById(rootView, id);
      if (logo == null) {
        break missingId;
      }

      ConstraintLayout mainContainer = (ConstraintLayout) rootView;

      id = R.id.or_container;
      LinearLayout orContainer = ViewBindings.findChildViewById(rootView, id);
      if (orContainer == null) {
        break missingId;
      }

      id = R.id.orTextView;
      TextView orTextView = ViewBindings.findChildViewById(rootView, id);
      if (orTextView == null) {
        break missingId;
      }

      id = R.id.outlookButton;
      MaterialButton outlookButton = ViewBindings.findChildViewById(rootView, id);
      if (outlookButton == null) {
        break missingId;
      }

      id = R.id.password;
      TextView password = ViewBindings.findChildViewById(rootView, id);
      if (password == null) {
        break missingId;
      }

      id = R.id.passwordEditText;
      EditText passwordEditText = ViewBindings.findChildViewById(rootView, id);
      if (passwordEditText == null) {
        break missingId;
      }

      id = R.id.signInButton;
      MaterialButton signInButton = ViewBindings.findChildViewById(rootView, id);
      if (signInButton == null) {
        break missingId;
      }

      id = R.id.start_line;
      TextView startLine = ViewBindings.findChildViewById(rootView, id);
      if (startLine == null) {
        break missingId;
      }

      id = R.id.titleTextView;
      TextView titleTextView = ViewBindings.findChildViewById(rootView, id);
      if (titleTextView == null) {
        break missingId;
      }

      id = R.id.vat_id;
      TextView vatId = ViewBindings.findChildViewById(rootView, id);
      if (vatId == null) {
        break missingId;
      }

      id = R.id.vat_idEditText;
      EditText vatIdEditText = ViewBindings.findChildViewById(rootView, id);
      if (vatIdEditText == null) {
        break missingId;
      }

      id = R.id.welcomeTextView;
      TextView welcomeTextView = ViewBindings.findChildViewById(rootView, id);
      if (welcomeTextView == null) {
        break missingId;
      }

      return new ActivityLoginBinding((ConstraintLayout) rootView, constraintLayout,
          constraintLayout2, endLine, forgotPasswordTextView, logo, mainContainer, orContainer,
          orTextView, outlookButton, password, passwordEditText, signInButton, startLine,
          titleTextView, vatId, vatIdEditText, welcomeTextView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
