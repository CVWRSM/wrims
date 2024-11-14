/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarSum;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SumContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>SVar Sum</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.SVarSumImpl#getSumContent <em>Sum Content</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SVarSumImpl extends SVarImpl implements SVarSum
{
  /**
   * The cached value of the '{@link #getSumContent() <em>Sum Content</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSumContent()
   * @generated
   * @ordered
   */
  protected SumContent sumContent;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SVarSumImpl()
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
    return WreslEditorPackage.Literals.SVAR_SUM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SumContent getSumContent()
  {
    return sumContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSumContent(SumContent newSumContent, NotificationChain msgs)
  {
    SumContent oldSumContent = sumContent;
    sumContent = newSumContent;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.SVAR_SUM__SUM_CONTENT, oldSumContent, newSumContent);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSumContent(SumContent newSumContent)
  {
    if (newSumContent != sumContent)
    {
      NotificationChain msgs = null;
      if (sumContent != null)
        msgs = ((InternalEObject)sumContent).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.SVAR_SUM__SUM_CONTENT, null, msgs);
      if (newSumContent != null)
        msgs = ((InternalEObject)newSumContent).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.SVAR_SUM__SUM_CONTENT, null, msgs);
      msgs = basicSetSumContent(newSumContent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.SVAR_SUM__SUM_CONTENT, newSumContent, newSumContent));
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
      case WreslEditorPackage.SVAR_SUM__SUM_CONTENT:
        return basicSetSumContent(null, msgs);
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
      case WreslEditorPackage.SVAR_SUM__SUM_CONTENT:
        return getSumContent();
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
      case WreslEditorPackage.SVAR_SUM__SUM_CONTENT:
        setSumContent((SumContent)newValue);
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
      case WreslEditorPackage.SVAR_SUM__SUM_CONTENT:
        setSumContent((SumContent)null);
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
      case WreslEditorPackage.SVAR_SUM__SUM_CONTENT:
        return sumContent != null;
    }
    return super.eIsSet(featureID);
  }

} //SVarSumImpl
