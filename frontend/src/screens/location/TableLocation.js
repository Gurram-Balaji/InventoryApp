import { forwardRef, Fragment } from 'react';
import { IconButton } from '@mui/material';

import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { TableVirtuoso } from 'react-virtuoso';
import { Riple } from 'react-loading-indicators';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import TableCell from '@mui/material/TableCell';

export default function TableLocation({ handleEditOpen, handleDeleteOpen, loading, location }) {

    const columns = [
        { width: 10, label: 'Id', dataKey: 'locationId', numeric: true },
        { width: 30, label: 'Description', dataKey: 'locationDesc' },
        { width: 50, label: 'Type', dataKey: 'locationType' },
        { width: 80, label: 'Address', dataKey: 'AddressLines' },
        { width: 40, label: 'Fulfillment', dataKey: 'fulfillment', numeric: true },
        { width: 20, label: 'Action', dataKey: 'action', numeric: true },
    ];
    const rowContent = (_index, row) => (
        <Fragment>
            {columns.map((column) => (
                <TableCell key={column.dataKey} align={column.numeric ? 'center' : 'left'}>
                    {column.dataKey === 'action' ? (
                        <>
                            <IconButton onClick={() => handleEditOpen(row)}><EditIcon /></IconButton>
                            <IconButton onClick={() => handleDeleteOpen(row)}><DeleteIcon /></IconButton>
                        </>
                    ) :column.dataKey === 'locationType' ? (row[column.dataKey] || '').toString().replace(/_/g, ' ') :row[column.dataKey]}
                </TableCell>
            ))}
        </Fragment>
    );

    return (<Paper style={{ height: 685, width: 1200, justifyContent: 'center', alignItems: 'center', display: 'flex', margin: '20px' }}>
        {loading && <Riple color="#803bec" size="large" /> }
        {!loading && location.length > 0 ? (

            <TableVirtuoso
                data={location}
                components={{
                    Scroller: forwardRef((props, ref) => (
                        <TableContainer component={Paper} {...props} ref={ref} />
                    )),
                    Table: (props) => (
                        <Table {...props} sx={{ borderCollapse: 'separate', tableLayout: 'fixed' }} />
                    ),
                    TableHead: forwardRef((props, ref) => <TableHead {...props} ref={ref} />),
                    TableRow,
                    TableBody: forwardRef((props, ref) => <TableBody {...props} ref={ref} />),
                }}

                fixedHeaderContent={() => (
                    <TableRow>
                        {columns.map((column) => (
                            <TableCell
                                key={column.dataKey}
                                variant="head"
                                align={column.numeric ? 'center' : 'left'}
                                style={{ width: column.width, backgroundColor: "black", color: "white", fontWeight: "Bold", textTransform: 'uppercase' }}
                                sx={{ backgroundColor: 'background.paper' }}
                            >
                                {column.label}
                            </TableCell>
                        ))}
                    </TableRow>
                )}
                itemContent={rowContent}
            />
        ) : (
            !loading && <p>No locations found.</p>
        )}
    </Paper>
    );
}