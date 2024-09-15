import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITaskAttachment } from '../task-attachment.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../task-attachment.test-samples';

import { TaskAttachmentService } from './task-attachment.service';

const requireRestSample: ITaskAttachment = {
  ...sampleWithRequiredData,
};

describe('TaskAttachment Service', () => {
  let service: TaskAttachmentService;
  let httpMock: HttpTestingController;
  let expectedResult: ITaskAttachment | ITaskAttachment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TaskAttachmentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a TaskAttachment', () => {
      const taskAttachment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(taskAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TaskAttachment', () => {
      const taskAttachment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(taskAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TaskAttachment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TaskAttachment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TaskAttachment', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTaskAttachmentToCollectionIfMissing', () => {
      it('should add a TaskAttachment to an empty array', () => {
        const taskAttachment: ITaskAttachment = sampleWithRequiredData;
        expectedResult = service.addTaskAttachmentToCollectionIfMissing([], taskAttachment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskAttachment);
      });

      it('should not add a TaskAttachment to an array that contains it', () => {
        const taskAttachment: ITaskAttachment = sampleWithRequiredData;
        const taskAttachmentCollection: ITaskAttachment[] = [
          {
            ...taskAttachment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTaskAttachmentToCollectionIfMissing(taskAttachmentCollection, taskAttachment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TaskAttachment to an array that doesn't contain it", () => {
        const taskAttachment: ITaskAttachment = sampleWithRequiredData;
        const taskAttachmentCollection: ITaskAttachment[] = [sampleWithPartialData];
        expectedResult = service.addTaskAttachmentToCollectionIfMissing(taskAttachmentCollection, taskAttachment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskAttachment);
      });

      it('should add only unique TaskAttachment to an array', () => {
        const taskAttachmentArray: ITaskAttachment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const taskAttachmentCollection: ITaskAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addTaskAttachmentToCollectionIfMissing(taskAttachmentCollection, ...taskAttachmentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const taskAttachment: ITaskAttachment = sampleWithRequiredData;
        const taskAttachment2: ITaskAttachment = sampleWithPartialData;
        expectedResult = service.addTaskAttachmentToCollectionIfMissing([], taskAttachment, taskAttachment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskAttachment);
        expect(expectedResult).toContain(taskAttachment2);
      });

      it('should accept null and undefined values', () => {
        const taskAttachment: ITaskAttachment = sampleWithRequiredData;
        expectedResult = service.addTaskAttachmentToCollectionIfMissing([], null, taskAttachment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskAttachment);
      });

      it('should return initial array if no TaskAttachment is added', () => {
        const taskAttachmentCollection: ITaskAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addTaskAttachmentToCollectionIfMissing(taskAttachmentCollection, undefined, null);
        expect(expectedResult).toEqual(taskAttachmentCollection);
      });
    });

    describe('compareTaskAttachment', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTaskAttachment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTaskAttachment(entity1, entity2);
        const compareResult2 = service.compareTaskAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTaskAttachment(entity1, entity2);
        const compareResult2 = service.compareTaskAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTaskAttachment(entity1, entity2);
        const compareResult2 = service.compareTaskAttachment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
