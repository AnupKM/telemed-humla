import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { KeyValuePipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PatientRecordService } from '@core/services/patient-record';
import { PatientRecordModel } from '@shared/models/patientRecord';
import { PatientStateService } from '@core/services/patient-state';
import { PatientService } from '@core/services/patient';

interface ExpandableRecord extends PatientRecordModel {
  expanded?: boolean;
}

@Component({
  selector: 'app-pt-history',
  standalone: true,
  imports: [KeyValuePipe, DatePipe, FormsModule],
  templateUrl: './pt-history.html',
  styleUrl: './pt-history.scss',
})
export class PtHistory implements OnInit {
  private route = inject(ActivatedRoute);
  private historyService = inject(PatientRecordService);
  private patientService = inject(PatientService);
  private ptState = inject(PatientStateService);

  selectedPatient = this.ptState.selectedPatient;
  records = signal<ExpandableRecord[]>([]);
  isLoading = signal(true);
  isSaving = signal(false);

  isFormOpen = signal(false);
  isEditMode = signal(false);

  currentRecord = {
    id: '',
    history: '',
    diagnosis: '',
    plan: '',
    createdAt: ''
  };

  ngOnInit() {
    const patientId = this.route.snapshot.paramMap.get('patientId');

    if (patientId) {
      if (!this.selectedPatient()) {
        this.patientService.getPatientById(patientId).subscribe(pt => {
          this.ptState.setPatient(pt);
        });
      }
      this.fetchPtHistory(patientId);
    }
  }

  private fetchPtHistory(patientId: string) {
    this.isLoading.set(true);
    this.historyService.getRecordsByPatientId(patientId).subscribe({
      next: (data) => {
        const mappedData = data.map(record => ({ ...record, expanded: false }));
        this.records.set(mappedData);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  toggleRecord(id: string) {
    this.records.update(records =>
      records.map(r => r.id === id ? { ...r, expanded: !r.expanded } : r)
    );
  }

  onAddRecord() {
    if (this.currentRecord.id) {
      this.resetForm();
    }
    this.isEditMode.set(false);
    this.isFormOpen.set(true);
  }

  onEditRecord(record: ExpandableRecord) {
    this.isEditMode.set(true);
    this.currentRecord = {
      id: record.id,
      history: record.patientHistory['History'] || '',
      diagnosis: record.patientHistory['Diagnosis'] || '',
      plan: record.patientHistory['Plan'] || '',
      createdAt: record.createdAt
    };
    this.isFormOpen.set(true);
  }

  saveRecord() {
    const patient = this.selectedPatient();
    if (!patient) return;

    this.isSaving.set(true);

    const recordPayload: PatientRecordModel = {
      id: this.currentRecord.id,
      patientId: patient.id,
      patientFullName: `${patient.firstName} ${patient.lastName}`,
      recordCreatedByFullName: 'Current Physician',
      patientAge: patient.age,
      patientGender: patient.gender,
      patientHistory: {
        'History': this.currentRecord.history,
        'Diagnosis': this.currentRecord.diagnosis,
        'Plan': this.currentRecord.plan
      },
      updateAt: new Date().toISOString(),
      createdAt: this.isEditMode() ? this.currentRecord.createdAt : new Date().toISOString()
    };

    const operation$ = this.isEditMode()
      ? this.historyService.editRecord(recordPayload)
      : this.historyService.addRecord(recordPayload);

    operation$.subscribe({
      next: () => {
        this.handleSuccess();
        this.isSaving.set(false);
      },
      error: (err) => {
        console.error('Save failed', err);
        this.isSaving.set(false);
      }
    });
  }

  private handleSuccess() {
    this.isFormOpen.set(false);
    this.resetForm();
    const patientId = this.route.snapshot.paramMap.get('patientId');
    if (patientId) this.fetchPtHistory(patientId);
  }

  private resetForm() {
    this.currentRecord = { id: '', history: '', diagnosis: '', plan: '', createdAt: '' };
  }

  closeForm() {
    this.isFormOpen.set(false);
  }

  cancelForm() {
    if (confirm('Discard unsaved changes?')) {
      this.resetForm();
      this.isFormOpen.set(false);
    }
  }

  onDownloadPdf(record: PatientRecordModel) {

    this.historyService.downloadPdf(record.id).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;

        const pt = this.selectedPatient();
        const fileName = pt
          ? `Medical_Record_${pt.lastName}_${record.createdAt}.pdf`
          : `Medical_Record_${record.id}.pdf`;

        link.download = fileName;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Download failed', err);
      }
    });
  }

  onDeleteRecord(id: string) {
    if (confirm('Are you sure you want to delete this clinical record?')) {
      this.historyService.deleteRecord(id).subscribe({
        next: () => {
          this.records.update(list => list.filter(r => r.id !== id));
        },
        error: (err) => console.error('Delete failed', err)
      });
    }
  }
}