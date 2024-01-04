/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Add;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Multiply;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Add</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.AddImpl#getM1 <em>M1</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.AddImpl#getM2 <em>M2</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AddImpl extends ExpressionImpl implements Add
{
  /**
   * The cached value of the '{@link #getM1() <em>M1</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getM1()
   * @generated
   * @ordered
   */
  protected Multiply m1;

  /**
   * The cached value of the '{@link #getM2() <em>M2</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getM2()
   * @generated
   * @ordered
   */
  protected EList<Multiply> m2;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AddImpl()
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
    return WreslEditorPackage.Literals.ADD;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Multiply getM1()
  {
    return m1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetM1(Multiply newM1, NotificationChain msgs)
  {
    Multiply oldM1 = m1;
    m1 = newM1;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.ADD__M1, oldM1, newM1);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setM1(Multiply newM1)
  {
    if (newM1 != m1)
    {
      NotificationChain msgs = null;
      if (m1 != null)
        msgs = ((InternalEObject)m1).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.ADD__M1, null, msgs);
      if (newM1 != null)
        msgs = ((InternalEObject)newM1).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.ADD__M1, null, msgs);
      msgs = basicSetM1(newM1, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.ADD__M1, newM1, newM1));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Multiply> getM2()
  {
    if (m2 == null)
    {
      m2 = new EObjectContainmentEList<Multiply>(Multiply.class, this, WreslEditorPackage.ADD__M2);
    }
    return m2;
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
      case WreslEditorPackage.ADD__M1:
        return basicSetM1(null, msgs);
      case WreslEditorPackage.ADD__M2:
        return ((InternalEList<?>)getM2()).basicRemove(otherEnd, msgs);
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
      case WreslEditorPackage.ADD__M1:
        return getM1();
      case WreslEditorPackage.ADD__M2:
        return getM2();
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
      case WreslEditorPackage.ADD__M1:
        setM1((Multiply)newValue);
        return;
      case WreslEditorPackage.ADD__M2:
        getM2().clear();
        getM2().addAll((Collection<? extends Multiply>)newValue);
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
      case WreslEditorPackage.ADD__M1:
        setM1((Multiply)null);
        return;
      case WreslEditorPackage.ADD__M2:
        getM2().clear();
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
      case WreslEditorPackage.ADD__M1:
        return m1 != null;
      case WreslEditorPackage.ADD__M2:
        return m2 != null && !m2.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //AddImpl
