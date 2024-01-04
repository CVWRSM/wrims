/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Declaration;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Model;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModel;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Var Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.VarModelImpl#getRef1 <em>Ref1</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.VarModelImpl#getRef2 <em>Ref2</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class VarModelImpl extends FunctionImpl implements VarModel
{
  /**
   * The cached value of the '{@link #getRef1() <em>Ref1</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRef1()
   * @generated
   * @ordered
   */
  protected Declaration ref1;

  /**
   * The cached value of the '{@link #getRef2() <em>Ref2</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRef2()
   * @generated
   * @ordered
   */
  protected Model ref2;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected VarModelImpl()
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
    return WreslEditorPackage.Literals.VAR_MODEL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Declaration getRef1()
  {
    if (ref1 != null && ref1.eIsProxy())
    {
      InternalEObject oldRef1 = (InternalEObject)ref1;
      ref1 = (Declaration)eResolveProxy(oldRef1);
      if (ref1 != oldRef1)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, WreslEditorPackage.VAR_MODEL__REF1, oldRef1, ref1));
      }
    }
    return ref1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Declaration basicGetRef1()
  {
    return ref1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRef1(Declaration newRef1)
  {
    Declaration oldRef1 = ref1;
    ref1 = newRef1;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.VAR_MODEL__REF1, oldRef1, ref1));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model getRef2()
  {
    if (ref2 != null && ref2.eIsProxy())
    {
      InternalEObject oldRef2 = (InternalEObject)ref2;
      ref2 = (Model)eResolveProxy(oldRef2);
      if (ref2 != oldRef2)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, WreslEditorPackage.VAR_MODEL__REF2, oldRef2, ref2));
      }
    }
    return ref2;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model basicGetRef2()
  {
    return ref2;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRef2(Model newRef2)
  {
    Model oldRef2 = ref2;
    ref2 = newRef2;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.VAR_MODEL__REF2, oldRef2, ref2));
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
      case WreslEditorPackage.VAR_MODEL__REF1:
        if (resolve) return getRef1();
        return basicGetRef1();
      case WreslEditorPackage.VAR_MODEL__REF2:
        if (resolve) return getRef2();
        return basicGetRef2();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case WreslEditorPackage.VAR_MODEL__REF1:
        setRef1((Declaration)newValue);
        return;
      case WreslEditorPackage.VAR_MODEL__REF2:
        setRef2((Model)newValue);
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
      case WreslEditorPackage.VAR_MODEL__REF1:
        setRef1((Declaration)null);
        return;
      case WreslEditorPackage.VAR_MODEL__REF2:
        setRef2((Model)null);
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
      case WreslEditorPackage.VAR_MODEL__REF1:
        return ref1 != null;
      case WreslEditorPackage.VAR_MODEL__REF2:
        return ref2 != null;
    }
    return super.eIsSet(featureID);
  }

} //VarModelImpl
