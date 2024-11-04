import { Fragment } from 'react';
import { IconButton, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
import { Riple } from 'react-loading-indicators';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

export default function TableLocation({ handleEditOpen, handleDeleteOpen, loading, location }) {

    const columns = [
        { width: 10, label: 'Id', dataKey: 'locationId', numeric: true },
        { width: 30, label: 'Description', dataKey: 'locationDesc' },
        { width: 50, label: 'Type', dataKey: 'locationType' },
        { width: 80, label: 'Address', dataKey: 'AddressLines' },
        { width: 40, label: 'Fulfillment', dataKey: 'fulfillment' },
        { width: 20, label: 'Action', dataKey: 'action', numeric: true },
    ];

    const rowContent = (row) => (
        <TableRow key={row.locationId}>
            {columns.map((column) => (
                <TableCell key={column.dataKey} align={column.numeric ? 'center' : 'left'}>
                    {column.dataKey === 'action' ? (
                        <>
                            <IconButton onClick={() => handleEditOpen(row)}><EditIcon /></IconButton>
                            <IconButton onClick={() => handleDeleteOpen(row)}><DeleteIcon /></IconButton>
                        </>
                    ) : column.dataKey === 'locationType' ? (row[column.dataKey]).toString().replace(/_/g, ' ')
                        : row[column.dataKey]}
                </TableCell>
            ))}
        </TableRow>
    );

    return (
        <Paper style={{ height: 690, width: 1200,  display: 'flex', margin: '20px' }}>
            {loading && <Riple color="#803bec" size="large" />}
            {!loading && location.length > 0 ? (
                <TableContainer component={Paper}>
                    <Table sx={{ borderCollapse: 'separate', tableLayout: 'fixed' }}>
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell
                                        key={column.dataKey}
                                        align={column.numeric ? 'center' : 'left'}
                                        style={{ width: column.width, backgroundColor: "black", color: "white", fontWeight: "Bold", textTransform: 'uppercase' }}
                                    >
                                        {column.label}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {location.map((row) => rowContent(row))}
                        </TableBody>
                    </Table>
                </TableContainer>
            ) : (
                !loading && <p>No locations found.</p>
            )}
        </Paper>
    );
}
