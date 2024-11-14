/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Multiply;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Unary;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Multiply</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.MultiplyImpl#getU1 <em>U1</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.MultiplyImpl#getU2 <em>U2</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MultiplyImpl extends MinimalEObjectImpl.Container implements Multiply
{
  /**
   * The cached value of the '{@link #getU1() <em>U1</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getU1()
   * @generated
   * @ordered
   */
  protected Unary u1;

  /**
   * The cached value of the '{@link #getU2() <em>U2</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getU2()
   * @generated
   * @ordered
   */
  protected EList<Unary> u2;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MultiplyImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return WreslEditorPackage.Literals.MULTIPLY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Unary getU1()
  {
    return u1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetU1(Unary newU1, NotificationChain msgs)
  {
    Unary oldU1 = u1;
    u1 = newU1;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.MULTIPLY__U1, oldU1, newU1);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setU1(Unary newU1)
  {
    if (newU1 != u1)
    {
      NotificationChain msgs = null;
      if (u1 != null)
        msgs = ((InternalEObject)u1).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.MULTIPLY__U1, null, msgs);
      if (newU1 != null)
        msgs = ((InternalEObject)newU1).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.MULTIPLY__U1, null, msgs);
      msgs = basicSetU1(newU1, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.MULTIPLY__U1, newU1, newU1));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Unary> getU2()
  {
    if (u2 == null)
    {
      u2 = new EObjectContainmentEList<Unary>(Unary.class, this, WreslEditorPackage.MULTIPLY__U2);
    }
    return u2;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case WreslEditorPackage.MULTIPLY__U1:
        return basicSetU1(null, msgs);
      case WreslEditorPackage.MULTIPLY__U2:
        return ((InternalEList<?>)getU2()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case WreslEditorPackage.MULTIPLY__U1:
        return getU1();
      case WreslEditorPackage.MULTIPLY__U2:
        return getU2();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case WreslEditorPackage.MULTIPLY__U1:
        setU1((Unary)newValue);
        return;
      case WreslEditorPackage.MULTIPLY__U2:
        getU2().clear();
        getU2().addAll((Collection<? extends Unary>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case WreslEditorPackage.MULTIPLY__U1:
        setU1((Unary)null);
        return;
      case WreslEditorPackage.MULTIPLY__U2:
        getU2().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case WreslEditorPackage.MULTIPLY__U1:
        return u1 != null;
      case WreslEditorPackage.MULTIPLY__U2:
        return u2 != null && !u2.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //MultiplyImpl
