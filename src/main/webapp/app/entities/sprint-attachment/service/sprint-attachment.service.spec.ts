import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISprintAttachment } from '../sprint-attachment.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sprint-attachment.test-samples';

import { SprintAttachmentService } from './sprint-attachment.service';

const requireRestSample: ISprintAttachment = {
  ...sampleWithRequiredData,
};

describe('SprintAttachment Service', () => {
  let service: SprintAttachmentService;
  let httpMock: HttpTestingController;
  let expectedResult: ISprintAttachment | ISprintAttachment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SprintAttachmentService);
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

    it('should create a SprintAttachment', () => {
      const sprintAttachment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sprintAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SprintAttachment', () => {
      const sprintAttachment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sprintAttachment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SprintAttachment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SprintAttachment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SprintAttachment', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSprintAttachmentToCollectionIfMissing', () => {
      it('should add a SprintAttachment to an empty array', () => {
        const sprintAttachment: ISprintAttachment = sampleWithRequiredData;
        expectedResult = service.addSprintAttachmentToCollectionIfMissing([], sprintAttachment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sprintAttachment);
      });

      it('should not add a SprintAttachment to an array that contains it', () => {
        const sprintAttachment: ISprintAttachment = sampleWithRequiredData;
        const sprintAttachmentCollection: ISprintAttachment[] = [
          {
            ...sprintAttachment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSprintAttachmentToCollectionIfMissing(sprintAttachmentCollection, sprintAttachment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SprintAttachment to an array that doesn't contain it", () => {
        const sprintAttachment: ISprintAttachment = sampleWithRequiredData;
        const sprintAttachmentCollection: ISprintAttachment[] = [sampleWithPartialData];
        expectedResult = service.addSprintAttachmentToCollectionIfMissing(sprintAttachmentCollection, sprintAttachment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sprintAttachment);
      });

      it('should add only unique SprintAttachment to an array', () => {
        const sprintAttachmentArray: ISprintAttachment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sprintAttachmentCollection: ISprintAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addSprintAttachmentToCollectionIfMissing(sprintAttachmentCollection, ...sprintAttachmentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sprintAttachment: ISprintAttachment = sampleWithRequiredData;
        const sprintAttachment2: ISprintAttachment = sampleWithPartialData;
        expectedResult = service.addSprintAttachmentToCollectionIfMissing([], sprintAttachment, sprintAttachment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sprintAttachment);
        expect(expectedResult).toContain(sprintAttachment2);
      });

      it('should accept null and undefined values', () => {
        const sprintAttachment: ISprintAttachment = sampleWithRequiredData;
        expectedResult = service.addSprintAttachmentToCollectionIfMissing([], null, sprintAttachment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sprintAttachment);
      });

      it('should return initial array if no SprintAttachment is added', () => {
        const sprintAttachmentCollection: ISprintAttachment[] = [sampleWithRequiredData];
        expectedResult = service.addSprintAttachmentToCollectionIfMissing(sprintAttachmentCollection, undefined, null);
        expect(expectedResult).toEqual(sprintAttachmentCollection);
      });
    });

    describe('compareSprintAttachment', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSprintAttachment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSprintAttachment(entity1, entity2);
        const compareResult2 = service.compareSprintAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSprintAttachment(entity1, entity2);
        const compareResult2 = service.compareSprintAttachment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSprintAttachment(entity1, entity2);
        const compareResult2 = service.compareSprintAttachment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
