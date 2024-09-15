import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStatusCategory } from 'app/entities/status-category/status-category.model';
import { StatusCategoryService } from 'app/entities/status-category/service/status-category.service';
import { IStatus } from '../status.model';
import { StatusService } from '../service/status.service';
import { StatusFormGroup, StatusFormService } from './status-form.service';

@Component({
  standalone: true,
  selector: 'sys-status-update',
  templateUrl: './status-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class StatusUpdateComponent implements OnInit {
  isSaving = false;
  status: IStatus | null = null;

  statusCategoriesSharedCollection: IStatusCategory[] = [];

  protected statusService = inject(StatusService);
  protected statusFormService = inject(StatusFormService);
  protected statusCategoryService = inject(StatusCategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StatusFormGroup = this.statusFormService.createStatusFormGroup();

  compareStatusCategory = (o1: IStatusCategory | null, o2: IStatusCategory | null): boolean =>
    this.statusCategoryService.compareStatusCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ status }) => {
      this.status = status;
      if (status) {
        this.updateForm(status);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const status = this.statusFormService.getStatus(this.editForm);
    if (status.id !== null) {
      this.subscribeToSaveResponse(this.statusService.update(status));
    } else {
      this.subscribeToSaveResponse(this.statusService.create(status));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStatus>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(status: IStatus): void {
    this.status = status;
    this.statusFormService.resetForm(this.editForm, status);

    this.statusCategoriesSharedCollection = this.statusCategoryService.addStatusCategoryToCollectionIfMissing<IStatusCategory>(
      this.statusCategoriesSharedCollection,
      status.category,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.statusCategoryService
      .query()
      .pipe(map((res: HttpResponse<IStatusCategory[]>) => res.body ?? []))
      .pipe(
        map((statusCategories: IStatusCategory[]) =>
          this.statusCategoryService.addStatusCategoryToCollectionIfMissing<IStatusCategory>(statusCategories, this.status?.category),
        ),
      )
      .subscribe((statusCategories: IStatusCategory[]) => (this.statusCategoriesSharedCollection = statusCategories));
  }
}
