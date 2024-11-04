import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import TableThreshold from '../TableThreshold'; // Adjust the import based on your file structure

describe('TableThreshold Component', () => {
    const handleEditOpen = jest.fn();
    const handleDeleteOpen = jest.fn();

    it('renders no threshold message when threshold is empty', () => {
        render(<TableThreshold loading={false} threshold={[]} handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} />);
        expect(screen.getByText(/no threshold found/i)).toBeInTheDocument();
    });

    it('renders threshold data correctly', async () => {
        const thresholdData = [
            { itemId: 1, locationId: 101, minThreshold: 10, maxThreshold: 50 },
            { itemId: 2, locationId: 102, minThreshold: 15, maxThreshold: 25 },
        ];

         await act(()=>render(<TableThreshold loading={false} threshold={thresholdData} handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} />));

        await waitFor(() => { // Check if the table cells render correct data
            expect(screen.getByText(1)).toBeInTheDocument();
            expect(screen.getByText(101)).toBeInTheDocument();
            expect(screen.getByText(10)).toBeInTheDocument();
            expect(screen.getByText(50)).toBeInTheDocument();
            expect(screen.getByText(2)).toBeInTheDocument();
            expect(screen.getByText(102)).toBeInTheDocument();
            expect(screen.getByText(15)).toBeInTheDocument();
            expect(screen.getByText(25)).toBeInTheDocument();
        });
    });

    it('triggers edit and delete actions', () => {
        const thresholdData = [
            { itemId: 1, locationId: 101, minThreshold: 10, maxThreshold: 50 },
        ];

        render(<TableThreshold loading={false} threshold={thresholdData} handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} />);

        const editButton = screen.getAllByRole('button')[0]; // Edit button for the first row
        const deleteButton = screen.getAllByRole('button')[1]; // Delete button for the first row

        fireEvent.click(editButton);
        expect(handleEditOpen).toHaveBeenCalledWith(thresholdData[0]);

        fireEvent.click(deleteButton);
        expect(handleDeleteOpen).toHaveBeenCalledWith(thresholdData[0]);
    });

    it('does not display loading indicator when loading is false', () => {
        render(<TableThreshold loading={false} threshold={[]} handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} />);
        expect(screen.queryByText(/loading/i)).not.toBeInTheDocument(); // Ensure loading indicator is not present
    });
});
