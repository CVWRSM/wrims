/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarTable;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.TableContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>SVar Table</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.SVarTableImpl#getTableContent <em>Table Content</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SVarTableImpl extends SVarImpl implements SVarTable
{
  /**
   * The cached value of the '{@link #getTableContent() <em>Table Content</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTableContent()
   * @generated
   * @ordered
   */
  protected TableContent tableContent;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SVarTableImpl()
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
    return WreslEditorPackage.Literals.SVAR_TABLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TableContent getTableContent()
  {
    return tableContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTableContent(TableContent newTableContent, NotificationChain msgs)
  {
    TableContent oldTableContent = tableContent;
    tableContent = newTableContent;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.SVAR_TABLE__TABLE_CONTENT, oldTableContent, newTableContent);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTableContent(TableContent newTableContent)
  {
    if (newTableContent != tableContent)
    {
      NotificationChain msgs = null;
      if (tableContent != null)
        msgs = ((InternalEObject)tableContent).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.SVAR_TABLE__TABLE_CONTENT, null, msgs);
      if (newTableContent != null)
        msgs = ((InternalEObject)newTableContent).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.SVAR_TABLE__TABLE_CONTENT, null, msgs);
      msgs = basicSetTableContent(newTableContent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.SVAR_TABLE__TABLE_CONTENT, newTableContent, newTableContent));
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
      case WreslEditorPackage.SVAR_TABLE__TABLE_CONTENT:
        return basicSetTableContent(null, msgs);
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
      case WreslEditorPackage.SVAR_TABLE__TABLE_CONTENT:
        return getTableContent();
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
      case WreslEditorPackage.SVAR_TABLE__TABLE_CONTENT:
        setTableContent((TableContent)newValue);
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
      case WreslEditorPackage.SVAR_TABLE__TABLE_CONTENT:
        setTableContent((TableContent)null);
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
      case WreslEditorPackage.SVAR_TABLE__TABLE_CONTENT:
        return tableContent != null;
    }
    return super.eIsSet(featureID);
  }

} //SVarTableImpl
